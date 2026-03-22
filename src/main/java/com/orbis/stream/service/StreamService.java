package com.orbis.stream.service;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.handler.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
Pacing in tempo reale: Il blocco Thread.sleep calcola la differenza tra il timestamp del video e il tempo effettivo trascorso dall'inizio del programma. Questo frena l'invio dei pacchetti in modo che venga riprodotto a velocità normale.

Sincronizzazione Timestamp: È stato aggiunto recorder.setTimestamp(timestamp); per assicurarsi che l'audio e il video viaggino sincronizzati e vengano letti correttamente da Twitch.

GOP Size (Keyframe): Aggiunto recorder.setGopSize((int) (fps * 2));. Twitch impone rigorosamente l'invio di un keyframe ogni 2 secondi, altrimenti la live risulta instabile.

Preset e Tune: Aggiunti "veryfast" e "zerolatency" per istruire l'encoder a minimizzare la latenza di elaborazione, essenziale per lo streaming live.

Parametri dinamici: Ora il metodo accetta l'URL locale e la key come stringhe, così puoi richiamarlo passandogli qualsiasi file video e qualsiasi account senza modificare il codice sorgente.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService {

    private final ResponseHandler responseHandler;
    private final LoggerMessageComponent loggerMessageComponent;

    private final TaskExecutor taskExecutor;


    public ResponseEntity<Map<String, String>> startLive(String inputPath, String twitchStreamKey) {
        String twitchUrl = "rtmp://live.twitch.tv/app/" + twitchStreamKey;

        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath);
            grabber.start();

            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            int audioChannels = grabber.getAudioChannels();
            double fps = grabber.getFrameRate();

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(twitchUrl, width, height, audioChannels);
            recorder.setFormat("flv");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setFrameRate(fps);
            recorder.setVideoBitrate(3_000_000);
            recorder.setAudioBitrate(160_000);
            recorder.setGopSize((int) (fps * 2));
            recorder.setVideoCodecName("libx264");

// Recupera il nome dell'encoder effettivamente caricato
            String encoderName = recorder.getVideoCodecName();

            if ("libx264".equalsIgnoreCase(encoderName)) {
                recorder.setVideoOption("preset", "veryfast");
                recorder.setVideoOption("tune", "zerolatency");
            } else {
                log.warn("L'encoder corrente è {}, salto le opzioni preset/tune per evitare errori", encoderName);
            }


            recorder.start();

            taskExecutor.execute(() -> {
                try {
                    Frame frame;
                    long startTime = System.currentTimeMillis();

                    while ((frame = grabber.grab()) != null) {
                        long timestamp = grabber.getTimestamp();
                        long timePassed = (System.currentTimeMillis() - startTime) * 1000;

                        if (timestamp > timePassed) {
                            Thread.sleep((timestamp - timePassed) / 1000);
                        }

                        recorder.setTimestamp(timestamp);
                        recorder.record(frame);
                    }
                } catch (Exception e) {
                    log.error("Errore durante lo streaming", e);
                } finally {
                    cleanup(grabber, recorder);
                }
            });
            return responseHandler.buildResponse("live.started", HttpStatus.ACCEPTED);
        }catch(Exception e){
            log.error(loggerMessageComponent.printMessage("error.starting.live"), e);
            return responseHandler.buildBadResponse("error.starting.live", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void cleanup(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder) {
        try {
            if (recorder != null) recorder.stop();
            if (grabber != null) grabber.stop();
        } catch (Exception ignored) {}
    }
}

