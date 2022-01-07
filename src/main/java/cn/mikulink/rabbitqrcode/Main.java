package cn.mikulink.rabbitqrcode;

import cn.mikulink.rabbitqrcode.normal.RabbitQRCode;
import cn.mikulink.rabbitqrcode.normal.RabbitQRCodeConfig;
import cn.mikulink.rabbitqrcode.visual.VisualQRCodeUtil;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * created by MikuNyanya on 2021/12/15 15:42
 * For the Reisen
 * 食用指南
 */
public class Main {
    public static void main(String[] args) {
        //二维码长度
        int width =500;
        //二维码宽度
        int height = 500;
        //二维码前景颜色
        Color onColor = new Color(0xFFFF96A0);
        //二维码背景颜色
        Color bgColor = new Color(0xFFFFFFFF);
        //二维码渐变 起始颜色
        Color onColorStart = new Color(0xFFFF96A0);
        //二维码渐变 结束颜色
        Color onColorEnd = new Color(0xFFFF69B4);
        //中心logo
        String logoUrl = "https://i1.hdslb.com/bfs/face/d973f5f34a9b102a9a0d6f216f806c225d66912f.jpg";
        //二维码内容
        String context = "https://tvax1.sinaimg.cn/large/ec43126fgy1gswm8zmzcqj20rs124tm9.jpg";
//        String context = "https://tvax2.sinaimg.cn//large/ec43126fgy1gt6b4v977uj20wv1ctjys.jpg";
        //二维码输出路径
        String outPutPath = "E:\\";

        try {
            //生成一个普通黑白二维码
//            BufferedImage qrImg = QRCodeUtil.createQRCode(context);
            //生成一个彩色二维码
//            qrImg = QRCodeService.createGradientColorQRCode(context, onColor, offColor);
            //生成一个自定义尺寸，中心带logo的彩色二维码
//            qrImg = QRCodeService.createQRCode(context, width, height, onColor, offColor, logoUrl);

            //生成一个渐变色二维码
//            qrImg = QRCodeService.createGradientColorQRCode(context, onColorStart, onColorEnd);
            //生成一个自定义尺寸，中心带logo的渐变色二维码
//            qrImg = QRCodeService.createGradientColorQRCode(context, width, height, onColorStart, onColorEnd, offColor, logoUrl);

            //生成本地文件
//            ImageIO.write(qrImg, "png", new File(outPutPath+"newQrtest1.png"));


            //视觉二维码相关
            //方块带背景
//            VisualQRCodeUtil.createQRCode(context, "E:\\logo.jpg", outPutPath + "POSITIONRECTANGLE.png", 'Q',
//                    new Color(2, 85, 43), null, null, null, true,
//                    VisualQRCode.POSITION_DETECTION_SHAPE_MODEL_RECTANGLE, VisualQRCode.FILL_SHAPE_MODEL_RECTANGLE);


            //圆点
//            VisualQRCodeUtil.createQRCode(context, "E:\\testqr.jpg", outPutPath+"FILLCIRCLE.png", 'Q',
//                    new Color(2, 85, 43), null, null, null, true,
//                    VisualQRCode.POSITION_DETECTION_SHAPE_MODEL_ROUND_RECTANGLE, VisualQRCode.FILL_SHAPE_MODEL_CIRCLE);

            //非
//            VisualQRCodeUtil.createQRCode(url, "./img/xmyrz.jpg", outPutPath+"LARGEIMG.png", 'M', new Color(170, 24, 67), 800, 420, 200, false,
//                    VisualQRCode.POSITION_DETECTION_SHAPE_MODEL_ROUND_RECTANGLE, VisualQRCode.FILL_SHAPE_MODEL_RECTANGLE);


            VisualQRCodeUtil.createQRCode(context, "E:\\logo.jpg", outPutPath+"setuqr2.png", 'H',
                    new Color(24, 153, 151, 255), null, null, null, true,
                    VisualQRCodeUtil.POSITION_DETECTION_SHAPE_MODEL_RECTANGLE, VisualQRCodeUtil.FILL_SHAPE_MODEL_CIRCLE);



            //兔子的二维码
//            RabbitQRCodeConfig qrCodeConfig = new RabbitQRCodeConfig();
//            //基本配置
//            qrCodeConfig.setWidth(width);   //尺寸 二维码方形的，一个参数即可
//            qrCodeConfig.setLevel(ErrorCorrectionLevel.Q); //容错等级
//            //颜色相关配置
//            qrCodeConfig.setColorType(RabbitQRCodeConfig.QRCODE_COLOR_TYPE_COLORFUL);   //二维码颜色类型
//            qrCodeConfig.setOnColor(onColorEnd);   //二维码颜色
////            qrCodeConfig.setBgColor(bgColor);   //背景颜色
//            qrCodeConfig.setGradientColorStart(onColorStart);   //渐变 开始
//            qrCodeConfig.setGradientColorEnd(onColorEnd);       //渐变 结束
//            //中间logo相关配置
////            qrCodeConfig.setLogoUrl(logoUrl);
////            qrCodeConfig.setLogoPath(""); //暂未启用
//            //背景图
//            qrCodeConfig.setBgImgPath("E:\\testqr.jpg");
//
//            BufferedImage image = RabbitQRCode.createQRCode(context, qrCodeConfig);
//
//            ImageIO.write(image, "png", new File(outPutPath + "rabbitQR.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
