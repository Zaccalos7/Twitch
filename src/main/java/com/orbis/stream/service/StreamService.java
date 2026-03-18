package com.orbis.stream.service;

import com.orbis.stream.handler.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.*;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StreamService {

    private final ResponseHandler responseHandler;

    public ResponseEntity<Map<String, String>> startLive() throws FrameGrabber.Exception, FFmpegFrameRecorder.Exception {

        // Abilita log dettagliati di FFmpeg
        FFmpegLogCallback.set();
        avutil.av_log_set_level(avutil.AV_LOG_INFO);

        String inputPath = "/Users/zaccalos/Movies/input.mp4";

        // Crea grabber
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath);
        grabber.setFormat("mp4");
        grabber.start();

        // Configura recorder
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                "rtmp://live.twitch.tv/app/live_1421368812_NOqfzzhID2sDFcdGgUwDOOmY7GOldJ",
                grabber.getImageWidth(),
                grabber.getImageHeight(),
                grabber.getAudioChannels()
        );
        recorder.setFormat("flv");
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);    // libx264
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);     // AAC
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setVideoBitrate(400_000);
        recorder.setAudioBitrate(64_000);

        recorder.start();

        // Ciclo robusto per leggere TUTTI i frame fino alla fine
        Frame frame;
        while (true) {
            frame = grabber.grabFrame();
            if (frame != null) {
                recorder.record(frame);
            } else {
                // Fallback: prova a leggere singoli frame video/audio ancora disponibili
                Frame videoFrame = grabber.grabImage();
                Frame audioFrame = grabber.grabSamples();

                if (videoFrame == null && audioFrame == null) {
                    break; // fine reale del video
                }

                if (videoFrame != null) recorder.record(videoFrame);
                if (audioFrame != null) recorder.record(audioFrame);
            }
        }

        // Chiude grabber e recorder
        recorder.stop();
        grabber.stop();

        return ResponseEntity.ok(Map.of("status", "streaming finished"));
    }

}
