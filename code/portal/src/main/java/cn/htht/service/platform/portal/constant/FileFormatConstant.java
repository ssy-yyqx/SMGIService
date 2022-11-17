package cn.htht.service.platform.portal.constant;

/**
 * 文件格式常量信息
 *
 * @author htht
 */
public enum FileFormatConstant {

    GRPGB001("CDR"),
    GRPGB002("WMF"),
    GRPGB003("EMF"),
    GRPGB004("SVG"),
    GRPGB005("EPS"),
    IMGG("图像媒体格式"),
    IMGGA001("BMP"),
    IMGGA002("GIF"),
    IMGGA003("PNG"),
    IMGGA004("JPG/JPEG"),
    IMGGB001("TIF/TIFF"),
    IMGGB002("JP2"),
    IMGGB003("PCD"),
    IMGGB004("PSD"),
    IMGGB005("PCX"),
    IMGGB006("TGA"),
    TXTGA("文本媒体格式"),
    TXTGA001("TXT"),
    TXTGA002("DOC"),
    FONGA("字体格式"),
    FONGA001("TTF"),
    FONGA002("TTC"),
    FONGA003("FON"),
    THDGB("三维模型格式"),
    THDGB001("DXF"),
    THDGB002("MA/MB"),
    THDGB003("MAX"),
    THDGB004("DWG"),
    THDGB005("WRL"),
    THDGB006("X3D"),
    THDGB007("MEL"),
    THDGB008("OBJ"),
    THDGB009("3DS"),
    AUDGA001("WAV"),
    AUDG("声音媒体格式"),
    AUDGA002("MP3"),
    AUDGA003("WMA"),
    GRPGB("图形媒体格式"),
    AUDGB001("MIDI"),
    AUDGB002("MOD"),
    AUDGB003("VQF"),
    AUDGB004("APE"),
    AUDGB005("RA/Ram/Rm"),
    VIDGA("影像媒体格式"),
    VIDGA001("MPEG"),
    VIDGA002("AVI"),
    VIDGA003("RM"),
    VIDGA004("WMV"),
    VIDGB001("ASF"),
    VIDGB002("MOV"),
    VIDGB003("RMVB"),
    VIDGB004("MP4"),
    VIDGB005("DivX"),
    VIDGB006("GIF"),
    CMPGA("复合文档格式"),
    CMPGA001("DOC"),
    CMPGA002("PDF"),
    CMPGA003("RTF"),
    CMPGA004("WPS"),
    DEMG("演示文稿格式"),
    DEMGA001("PPS"),
    DEMGA002("PPT"),
    DEMGA003("HTML"),
    DEMGA004("EXE"),
    DEMGA005("SWF"),
    DEMGB001("DIR"),
    DEMGB002("A4P"),
    DEMGB003("A5P"),
    DEMGB004("HTM"),
    DEMGB005("FLA"),
    DEMGB006("ATX、ATH");

    private String mediaFormat;

    FileFormatConstant(String mediaFormat) {
        this.mediaFormat = mediaFormat;
    }

    public String mediaFormat() {
        return mediaFormat;
    }
}
