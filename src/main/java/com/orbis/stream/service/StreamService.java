package com.orbis.stream.service;

import com.orbis.stream.handler.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<Map<String, String>> startLive(String inputPath, String twitchStreamKey) {

        String twitchUrl = "rtmp://live.twitch.tv/app/" + twitchStreamKey;

        FFmpegFrameGrabber grabber = null;
        FFmpegFrameRecorder recorder = null;

        try {
            avutil.av_log_set_level(avutil.AV_LOG_ERROR);

            grabber = new FFmpegFrameGrabber(inputPath);
            grabber.start();

            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            int audioChannels = grabber.getAudioChannels();
            double fps = grabber.getFrameRate();

            recorder = new FFmpegFrameRecorder(twitchUrl, width, height, audioChannels);
            recorder.setFormat("flv");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setFrameRate(fps);
            recorder.setVideoBitrate(3_000_000);
            recorder.setAudioBitrate(160_000);
            recorder.setGopSize((int) (fps * 2));
            recorder.setVideoOption("preset", "veryfast");
            recorder.setVideoOption("tune", "zerolatency");

            recorder.start();

            Frame frame;
            long videoFrames = 0;
            long audioFrames = 0;
            long startTime = System.currentTimeMillis();

            while ((frame = grabber.grab()) != null) {
                long timestamp = grabber.getTimestamp();
                long timePassed = (System.currentTimeMillis() - startTime) * 1000;

                if (timestamp > timePassed) {
                    Thread.sleep((timestamp - timePassed) / 1000);
                }

                recorder.setTimestamp(timestamp);
                recorder.record(frame);

                if (frame.image != null) videoFrames++;
                if (frame.samples != null) audioFrames++;
            }

            return ResponseEntity.ok(Map.of(
                    "status", "streaming finished",
                    "videoFrames", String.valueOf(videoFrames),
                    "audioFrames", String.valueOf(audioFrames)
            ));

        } catch (Exception e) {
            log.error("Errore streaming", e);
            return null;
        } finally {
            try {
                if (recorder != null) recorder.stop();
            } catch (Exception ignored) {}
            try {
                if (grabber != null) grabber.stop();
            } catch (Exception ignored) {}
        }
    }
}