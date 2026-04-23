package com.orbis.stream.enums;

import lombok.Getter;

@Getter
public enum PixelFormatEnum {

    YUV420P(0),
    YUYV422(1),
    RGB24(2),
    BGR24(3),
    YUV422P(4),
    YUV444P(5),
    YUV410P(6),
    YUV411P(7),
    GRAY8(8),
    MONOWHITE(9),
    MONOBLACK(10),
    PAL8(11),
    YUVJ420P(12),
    YUVJ422P(13),
    YUVJ444P(14),
    UYVY422(15),
    UYYVYY411(16),
    BGR8(17),
    BGR4(18),
    BGR4_BYTE(19),
    RGB8(20),
    RGB4(21),
    RGB4_BYTE(22),
    NV12(23),
    NV21(24),
    ARGB(25),
    RGBA(26),
    ABGR(27),
    BGRA(28),
    GRAY16BE(29),
    GRAY16LE(30),
    YUV440P(31),
    YUVJ440P(32),
    YUVA420P(33),
    RGB48BE(34),
    RGB48LE(35),
    RGB565BE(36),
    RGB565LE(37),
    RGB555BE(38),
    RGB555LE(39),
    BGR565BE(40),
    BGR565LE(41),
    BGR555BE(42),
    BGR555LE(43),
    VAAPI(44),
    YUV420P16LE(45),
    YUV420P16BE(46),
    YUV422P16LE(47),
    YUV422P16BE(48),
    YUV444P16LE(49),
    YUV444P16BE(50),
    DXVA2_VLD(51),
    RGB444LE(52),
    RGB444BE(53),
    BGR444LE(54),
    BGR444BE(55),
    YA8(56);

    private final int value;

    PixelFormatEnum(int value) {
        this.value = value;
    }
}