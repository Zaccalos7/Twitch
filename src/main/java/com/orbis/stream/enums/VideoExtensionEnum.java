package com.orbis.stream.enums;

import lombok.Getter;

@Getter
public enum VideoExtensionEnum {
    MP4(".mp4"),
    FLV(".flv"),
    MOV(".mov"),
    WEBM(".webm"),
    VP9(".vp9");

    private final String videoExtension;

    VideoExtensionEnum(String infoName) {
        this.videoExtension = infoName;
    }

    public static boolean isVideoExtensionPresent(String videoExtension){
        if(videoExtension == null){
            return false;
        }

        String videExtensionWithDot = "."+videoExtension;

        for (VideoExtensionEnum extensionEnum : VideoExtensionEnum.values()) {
            return videExtensionWithDot.equalsIgnoreCase(extensionEnum.getVideoExtension());
        }

        return false;
    }
}
