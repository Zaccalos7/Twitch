package com.orbis.stream.service;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.enums.*;
import com.orbis.stream.exceptions.FileReadingException;
import com.orbis.stream.exceptions.LiveException;
import com.orbis.stream.exceptions.NotFoundCustomException;
import com.orbis.stream.handler.ResponseHandler;
import com.orbis.stream.mapping.mapperGeneric.FromVideoRecordToStartLiveRecordMapper;
import com.orbis.stream.mapping.mapperRECORD.VideoLiveHistoryRecordMapper;
import com.orbis.stream.mapping.mapperRECORD.VideoSettingRecordMapper;
import com.orbis.stream.model.Video;
import com.orbis.stream.model.VideoLiveHistory;
import com.orbis.stream.model.VideoSetting;
import com.orbis.stream.model.VideoSettingsOption;
import com.orbis.stream.record.StartLiveRecord;
import com.orbis.stream.record.VideoLiveHistoryRecord;
import com.orbis.stream.record.VideoRecord;
import com.orbis.stream.record.VideoSettingsRecord;
import com.orbis.stream.repository.VideoLiveHistoryRepository;
import com.orbis.stream.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
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

    private final VideoSettingRecordMapper videoSettingRecordMapper;
    private final FromVideoRecordToStartLiveRecordMapper fromVideoRecordToStartLiveRecordMapper;
    private final VideoLiveHistoryRecordMapper videoLiveHistoryRecordMapper;

    private final VideoRepository videoRepository;
    private final VideoLiveHistoryRepository videoLiveHistoryRepository;

    private final TaskExecutor taskExecutor;


//    public ResponseEntity<Map<String, String>> startLive(String inputPath, String twitchStreamKey, StartLiveRecord startLiveRecord) {
//
//        String twitchUrl = "rtmp://live.twitch.tv/app/" + twitchStreamKey;
//
//        try {
//            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath);
//            grabber.start();
//
//            int width = grabber.getImageWidth();
//            int height = grabber.getImageHeight();
//            int audioChannels = grabber.getAudioChannels();
//            double fps = grabber.getFrameRate();
//
//            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(twitchUrl, width, height, audioChannels);
//            recorder.setFormat("flv");
//            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
//            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
//            recorder.setFrameRate(fps);
//            recorder.setVideoBitrate(3_000_000);
//            recorder.setAudioBitrate(160_000);
//            recorder.setGopSize((int) (fps * 2));
//            recorder.setVideoCodecName("libx264");
//
//// Recupera il nome dell'encoder effettivamente caricato
//            String encoderName = recorder.getVideoCodecName();
//
//            if ("libx264".equalsIgnoreCase(encoderName)) {
//                recorder.setVideoOption("preset", "veryfast");
//                recorder.setVideoOption("tune", "zerolatency");
//            } else {
//                log.warn("L'encoder corrente è {}, salto le opzioni preset/tune per evitare errori", encoderName);
//            }
//
//
//            recorder.start();
//
//            taskExecutor.execute(() -> {
//                try {
//                    Frame frame;
//                    long startTime = System.currentTimeMillis();
//
//                    while ((frame = grabber.grab()) != null) {
//                        long timestamp = grabber.getTimestamp();
//                        long timePassed = (System.currentTimeMillis() - startTime) * 1000;
//
//                        if (timestamp > timePassed) {
//                            Thread.sleep((timestamp - timePassed) / 1000);
//                        }
//
//                        recorder.setTimestamp(timestamp);
//                        recorder.record(frame);
//                    }
//                } catch (Exception e) {
//                    log.error("Errore durante lo streaming", e);
//                } finally {
//                    cleanup(grabber, recorder);
//                }
//            });
//            return responseHandler.buildResponse("live.started", HttpStatus.ACCEPTED);
//        }catch(Exception e){
//            log.error(loggerMessageComponent.printMessage("error.starting.live"), e);
//            return responseHandler.buildBadResponse("error.starting.live", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    private void cleanup(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder) {
        try {
            if (recorder != null) recorder.stop();
            if (grabber != null) grabber.stop();
        } catch (Exception ignored) {}
    }

    public ResponseEntity<Map<String, String>> startLive(StartLiveRecord startLiveRecord){

        String channelName = startLiveRecord.channelName();
        String platformStreamName = startLiveRecord.platformStreamName();

        checkIfALiveAlreadyStreamingForAChannel(channelName, platformStreamName);

        String videoPathFolder = startLiveRecord.videoPath();
        String streamKey = startLiveRecord.streamKey();
        String streamUrl = startLiveRecord.streamUrl();


        LocalDateTime timeStartLive = LocalDateTime.now();
        saveVideoLiveHistory(videoPathFolder, timeStartLive, streamUrl, streamKey, platformStreamName);

        VideoLiveHistory videoLiveHistory = retrievedVideoLiveHistorySaved(videoPathFolder, timeStartLive);

        saveVideoPaths(videoPathFolder, videoLiveHistory, startLiveRecord.videoSettingsRecord(), channelName);

        String streamingUrl;

        if(streamUrl.endsWith("/")){
            streamingUrl =  streamUrl.concat(streamKey);
        }else{
            streamingUrl =  streamUrl.concat("/").concat(streamKey);
        }

        return streamingVideo(videoLiveHistory, streamingUrl);
    }

    @Transactional(readOnly = true)
    protected void checkIfALiveAlreadyStreamingForAChannel(String channelName, String platformStreamName) {
        List<Video> videoList = videoRepository
                .findByLiveStatusAndChannelName(LiveStatusEnum.LIVE, channelName);

        if (videoList.isEmpty()) {
            return;
        }

        List<Video> videoListParsed = videoList.stream()
                .filter((video)-> {
                    boolean isCorrectedPlatform = video
                            .getVideoLiveHistory()
                            .getPlatformStreamName()
                            .equalsIgnoreCase(platformStreamName);
                    return isCorrectedPlatform;
                })
                .toList();

        if (videoListParsed.isEmpty()) {
            return;
        }

        throw new LiveException("channel.has.already.a.live.active");
    }

    private ResponseEntity<Map<String, String>> streamingVideo(VideoLiveHistory videoLiveHistory, String streamingUrl) {
        Long videoLiveHistoryId = videoLiveHistory.getPkid();
        List<Video> videoList = findAllVideoToStreamOnDirectory(videoLiveHistoryId);

        taskExecutor.execute(() -> {

            videoList.forEach((video -> {
                String inputPath = video.getVideoPath();
                Integer videoKey = video.getPkid();
                VideoSetting videoSetting = video.getVideoSetting();
                startVideoStreaming(streamingUrl, inputPath, videoSetting, videoLiveHistoryId, videoKey);
            }));
        });

        return responseHandler.buildResponse("live.started", HttpStatus.ACCEPTED);

    }

    @Transactional
    private List<Video> findAllVideoToStreamOnDirectory(Long videoLiveHistoryId) {
        return videoRepository.findByVideoLiveHistory_pkid(videoLiveHistoryId)
                .filter(videos -> !videos.isEmpty())
                .orElseThrow(()-> {
                    log.error("video.streaming.not.found");
                    return new NotFoundCustomException("video.streaming.not.found");
                });
    }

    private void startVideoStreaming(String twitchUrl,
                                     String inputPath,
                                     VideoSetting videoSetting,
                                     Long videoLiveHistoryId,
                                     Integer videoKey
                                     ){
        try {
            org.hibernate.Hibernate.initialize(videoSetting.getVideoSettingsOptions());
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath);
            grabber.start();

            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            int audioChannels = grabber.getAudioChannels();
            double fps = grabber.getFrameRate();

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(twitchUrl, width, height, audioChannels);
            recorder.setFormat(videoSetting.getVideoFormat());
            recorder.setVideoCodec(videoSetting.getVideoCodec());
            recorder.setAudioCodec(videoSetting.getAudioSetting().getAudioCodec());
            recorder.setPixelFormat(videoSetting.getPixelFormat());
            recorder.setFrameRate(fps);
            recorder.setVideoBitrate(videoSetting.getVideoBitrate());
            recorder.setAudioBitrate(videoSetting.getAudioSetting().getAudioBitrate());
            //gop size is the double of fps
            recorder.setGopSize((int) (fps * 2));
            recorder.setVideoCodecName(videoSetting.getVideoCodecName());

            List<VideoSettingsOption> videoSettingsOptionList = videoSetting.getVideoSettingsOptions();

            videoSettingsOptionList.forEach((videoSettingsOption)->{
                recorder.setVideoOption(videoSettingsOption.getKey(), videoSettingsOption.getValue());
            });

            recorder.start();
            log.info(loggerMessageComponent.printMessage("video.live.started", new Object[]{inputPath}));
            LocalDateTime dateTime = LocalDateTime.now();
            saveMessageOnVideoLiveHistory(loggerMessageComponent.printMessage("video.live.started", new Object[]{inputPath}), videoLiveHistoryId, inputPath, "LIVE", dateTime);

            try {
                Frame frame;
                long startTime = System.currentTimeMillis();
                boolean shouldBeStopped;

                while ((frame = grabber.grab()) != null) {
                    long timestamp = grabber.getTimestamp();
                    long timePassed = (System.currentTimeMillis() - startTime) * 1000;

                    shouldBeStopped = checkIfFlagStopExists(videoKey);

                    if(shouldBeStopped){
                        recorder.stop();
                        String message = loggerMessageComponent.printMessage("live.stopped");
                        log.info(message);
                        saveMessageOnVideoLiveHistory(message, videoLiveHistoryId, inputPath, "STOPPED", null);
                    }

                    //I send video datas faster than platform streaming
                    if (timestamp > timePassed) {
                        Thread.sleep((timestamp - timePassed) / 1000);
                    }

                    recorder.setTimestamp(timestamp);
                    recorder.record(frame);
                }
                String message = loggerMessageComponent.printMessage("video.live.ended");
                log.info(message);
                saveMessageOnVideoLiveHistory(message, videoLiveHistoryId, inputPath, "ENDED", null);

            } catch (Exception e) {
                String errorMessage = loggerMessageComponent.printMessage("error.during.streaming.video", new Object[]{inputPath})
                        + "\n"
                        + e.getMessage();
                log.error(errorMessage);
                dateTime = LocalDateTime.now();
                saveMessageOnVideoLiveHistory(errorMessage, videoLiveHistoryId, inputPath, "ERROR", dateTime);
            } finally {
                cleanup(grabber, recorder);
            }

        }catch(Exception e){
            String errorMessage = loggerMessageComponent.printMessage("error.during.starting.video", new Object[]{inputPath})
                    + "\n"
                    + e.getMessage();
            log.error(errorMessage);
            LocalDateTime dateTime = LocalDateTime.now();
            saveMessageOnVideoLiveHistory(errorMessage, videoLiveHistoryId, inputPath, "ERROR", dateTime);
        }
    }

    @Transactional(readOnly = true)
    private boolean checkIfFlagStopExists(Integer videoKey) {
        Video video = videoRepository.findByPkid(videoKey);
        return video.getShouldBeStop();
    }

    @Transactional
    private void saveMessageOnVideoLiveHistory(String message, Long videoLiveHistoryId, String inputPath, String statusLive, LocalDateTime dateTime) {
        Video video = videoRepository.findByVideoPathAndVideoLiveHistory_pkid(inputPath, videoLiveHistoryId);

        if(video == null){
            log.error(loggerMessageComponent.printMessage("error.during.retrieved.video", new Object[]{inputPath, videoLiveHistoryId}));
            return;
        }
        if(dateTime != null)
            video.setStartDateLive(dateTime);

        video.setMessage(message);

        video.setLiveStatus(LiveStatusEnum.valueOf(statusLive));
        videoRepository.save(video);
    }

    @Transactional
    private void saveVideoPaths(String videoPathFolder, VideoLiveHistory videoLiveHistory, VideoSettingsRecord videoSettingsRecord, String channelName) {
        File videoFile = Paths.get(videoPathFolder).toFile();

        VideoSetting videoSetting = videoSettingRecordMapper.toModel(videoSettingsRecord);

        if(videoFile.isDirectory()) {
            saveAllVideoPaths(videoFile, videoLiveHistory, videoSetting, channelName);
        }else{
            saveOneVideoPaths(videoFile, videoLiveHistory, videoSetting, channelName);
        }
    }

   
    private void saveAllVideoPaths(File videoFile, VideoLiveHistory videoLiveHistory, VideoSetting videoSetting, String channelName){

        File[] videoList = videoFile.listFiles(pathname -> {
            if(pathname.isDirectory()){
                return false;
            }
            String fileName = pathname.getName();
            int lastDotIndex = fileName.lastIndexOf(".");

            if (lastDotIndex == -1) {
                return false;
            }

            String extension = fileName.substring(lastDotIndex + 1);
            return VideoExtensionEnum.isVideoExtensionPresent(extension);
        });


        if(videoList == null || videoList.length == 0){
            log.error(loggerMessageComponent.printMessage("folder.empty", new Object[]{ videoFile.getAbsolutePath()}));
            throw new NotFoundCustomException("folder.empty", new Object[]{ videoFile.getAbsolutePath()});
        }

        for(File file : videoList){
            Video video = Video
                    .builder()
                    .name(file.getName())
                    .videoPath(file.getAbsolutePath())
                    .extension(extractExtensionFile(file.getName()))
                    .lastTimeStampBeforeStop(0L)
                    .liveStatus(LiveStatusEnum.OFFLINE)
                    .videoLiveHistory(videoLiveHistory)
                    .videoSetting(videoSetting)
                    .shouldBeStop(false)
                    .startDateLive(LocalDateTime.now())
                    .channelName(channelName)
                    .build();
            saveOnModelVideo(video);
        }
    }

    @Transactional(readOnly = true)
    private VideoLiveHistory retrievedVideoLiveHistorySaved(String videoFileAbsolutePath, LocalDateTime timeStartLive) {
        return videoLiveHistoryRepository.findByFolderOfVideoToStreamAndLocalDateTimeStartLive(videoFileAbsolutePath, timeStartLive)
                .orElseThrow( ()->{
                    log.error(loggerMessageComponent.printMessage("video.history.not.found"));
                    return new NotFoundCustomException("video.history.not.found");
                });
    }

    // for now user is always Mario
    @Transactional
    private void saveVideoLiveHistory(String videoFileAbsolutePath,
                                      LocalDateTime zoneIdTime,
                                      String streamUrl,
                                      String streamKey,
                                      String platformStreamingName
                                      ) {
        VideoLiveHistory videoLiveHistory = VideoLiveHistory
                .builder()
                .userName("Mario")
                .folderOfVideoToStream(videoFileAbsolutePath)
                .localDateTimeStartLive(zoneIdTime)
                .streamUrl(streamUrl)
                .streamKey(streamKey)
                .platformStreamName(platformStreamingName)
                .build();

        videoLiveHistoryRepository.save(videoLiveHistory);
    }

    private void saveOneVideoPaths(File videoFile, VideoLiveHistory videoLiveHistory, VideoSetting videoSetting, String channelName){
       

        Video video = Video
                .builder()
                .name(videoFile.getName())
                .videoPath(videoFile.getAbsolutePath())
                .extension(extractExtensionFile(videoFile.getName()))
                .lastTimeStampBeforeStop(0L)
                .liveStatus(LiveStatusEnum.OFFLINE)
                .videoLiveHistory(videoLiveHistory)
                .videoSetting(videoSetting)
                .shouldBeStop(false)
                .startDateLive(LocalDateTime.now())
                .channelName(channelName)
                .build();
        saveOnModelVideo(video);

    }

    private void saveOnModelVideo(Video video){
        videoRepository.save(video);
    }

    private String extractExtensionFile(String fileName){
        if(fileName == null){
            log.error(loggerMessageComponent.printMessage("extension.not.found"));
            throw new NotFoundCustomException("extension.not.found");
        }

        String[] files = fileName.split("\\.");

        if(files.length < 1 )
            throw new FileReadingException("video.not.valid", new Object[]{fileName});

        String extension = files[1];

        return extension;
    }

    public void stopVideoStreamingByPkid(Integer videoLivePkid) {
        Video video = checkIfExistsAndReturnEntity(videoLivePkid);
        video.setShouldBeStop(true);
        saveFlagToStopLive(video);
    }

    @Transactional(readOnly = true)
    protected Video checkIfExistsAndReturnEntity(Integer pkid){
        Video video =  videoRepository.findByPkid(pkid);
        if(video == null){
            log.warn(loggerMessageComponent.printMessage("video.not.found"));
            throw new NotFoundCustomException("video.not.found");
        }
        return video;
    }

    @Transactional
    protected void saveFlagToStopLive(Video video){
        videoRepository.save(video);
    }

    public ResponseEntity<Map<String, String>> startVideo(VideoRecord videoRecord){
        VideoLiveHistoryRecord videoLiveHistoryPkid = videoRecord.videoLiveHistory();
        StartLiveRecord startLiveRecord = fromVideoRecordToStartLiveRecordMapper.toStartLiveRecord(videoRecord);
        return startLiveWithoutSaveOnModel(startLiveRecord, videoLiveHistoryPkid);
    }

    /*
    / this method is used for play o restart a single video , with video options
      and video details already saved on db
     */
    private ResponseEntity<Map<String, String>> startLiveWithoutSaveOnModel(StartLiveRecord startLiveRecord, VideoLiveHistoryRecord videoLiveHistoryRecord){

        String channelName = startLiveRecord.channelName();
        String platformStreamName = startLiveRecord.platformStreamName();
        stopLives(channelName, platformStreamName);

        checkIfALiveAlreadyStreamingForAChannel(channelName, platformStreamName);

        String videoPathFolder = startLiveRecord.videoPath();
        String streamKey = startLiveRecord.streamKey();
        String streamUrl = startLiveRecord.streamUrl();


        LocalDateTime timeStartLive = LocalDateTime.now();
        saveVideoLiveHistory(videoPathFolder, timeStartLive, streamUrl, streamKey, platformStreamName);

        VideoLiveHistory videoLiveHistory = videoLiveHistoryRecordMapper.toModel(videoLiveHistoryRecord);

        String streamingUrl;

        if(streamUrl.endsWith("/")){
            streamingUrl =  streamUrl.concat(streamKey);
        }else{
            streamingUrl =  streamUrl.concat("/").concat(streamKey);
        }

        return streamingVideo(videoLiveHistory, streamingUrl);
    }


    public void stopLives(String channelName, String platformStream) {

        var videoToStoppedList = getAllVideoLiveForChannelNameAndPlatformName(channelName, platformStream);
        if(videoToStoppedList == null || videoToStoppedList.isEmpty())
            return;
        videoToStoppedList.forEach(this::saveFlagToStopLive);

    }

    @Transactional(readOnly = true)
    protected List<Video> getAllVideoLiveForChannelNameAndPlatformName(String channelName, String platformStreamName) {
        List<Video> videoList = videoRepository
                .findByLiveStatusAndChannelName(LiveStatusEnum.LIVE, channelName);

        if (videoList.isEmpty()) {
            return null;
        }

        List<Video> videoListParsed = videoList.stream()
                .filter((video)-> {
                    boolean isCorrectedPlatform = video
                            .getVideoLiveHistory()
                            .getPlatformStreamName()
                            .equalsIgnoreCase(platformStreamName);
                    return isCorrectedPlatform;
                })
                .toList();

        if (videoListParsed.isEmpty()) {
            return null;
        }

        return videoListParsed;
    }

}

