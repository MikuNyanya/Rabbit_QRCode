package cn.mikulink.rabbitqrcode.normal;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * created by MikuNyanya on 2022/1/5 11:20
 * For the Reisen
 * 二维码配置
 * 因为可传入参数太多，写成一个对象，方便使用
 */
@Getter
@Setter
public class RabbitQRCodeConfig {
    //默认二维码尺寸
    public static final int QRCODE_WIDTH_DEFAULE = 200;
    //    @Setter(AccessLevel.NONE)
    //    public static final int QRCODE_HEIGHT_DEFAULE = 200;
    //默认二维码颜色
    public static final Color ONCOLOR_DEFAULT = new Color(0xFF000000);
    public static final Color OFFCOLOR_DEFAULT = new Color(0xFFFFFFFF);
    public static final Color BGCOLOR_DEFAULT = new Color(0xFFFFFFFF);

    //二维码颜色类型
    //常规黑白
    public static final int QRCODE_COLOR_TYPE_NORMAL = 1;
    //彩色
    public static final int QRCODE_COLOR_TYPE_COLORFUL = 2;
    //渐变色
    public static final int QRCODE_COLOR_TYPE_GRADIENT = 3;

    /**
     * 二维码尺寸
     * 长宽一致
     */
    private int width = QRCODE_WIDTH_DEFAULE;

    /**
     * 纠错等级L/M/Q/H
     * 纠错等级越高越不易识别，数据量就越大，最高等级H
     * L(<7%),M(<15%),Q(<25%),H(<30%)
     */
    private ErrorCorrectionLevel level = ErrorCorrectionLevel.M;

    /**
     * 二维码颜色类型
     */
    private int colorType = QRCODE_COLOR_TYPE_NORMAL;
    /**
     * 二维码主色
     * 可以看见的二维码，包括定位码颜色
     */
    private Color onColor = ONCOLOR_DEFAULT;
    /**
     * 二维码副色
     */
    private Color offColor = OFFCOLOR_DEFAULT;
    /**
     * 二维码背景色
     */
    private Color bgColor = BGCOLOR_DEFAULT;
    /**
     * 渐变色起始颜色
     */
    private Color gradientColorStart = ONCOLOR_DEFAULT;
    /**
     * 渐变色结束颜色
     */
    private Color gradientColorEnd = ONCOLOR_DEFAULT;

//    /**
//     * 是否在中心添加logo
//     */
//    private boolean useLogo;
    /**
     * 中间logo图片网络链接
     * 优先使用该参数
     */
    private String logoUrl;
    /**
     * 中间logo本地路径
     * 如果logoUrl参数为空则使用该参数
     */
    private String logoPath;

    /**
     * 背景图网络链接
     * 优先使用该参数
     */
    private String bgImgurl;
    /**
     * 背景图本地路径
     * bgImgurl为空时则使用该参数
     */
    private String bgImgPath;

}
