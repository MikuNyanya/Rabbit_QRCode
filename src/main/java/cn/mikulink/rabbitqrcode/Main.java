package cn.mikulink.rabbitqrcode;

import cn.mikulink.rabbitqrcode.visual.VisualQRCodeUtil;

import java.awt.*;

/**
 * created by MikuNyanya on 2021/12/15 15:42
 * For the Reisen
 * 食用指南
 */
public class Main {
    public static void main(String[] args) {
        //二维码长度
        int width = 500;
        //二维码宽度
        int height = 500;
        //二维码前景颜色
        Color onColor = new Color(Integer.parseInt("FF96A0", 16));
        //二维码背景颜色
        Color offColor = new Color(Integer.parseInt("FFFFFF", 16));
        //二维码前景渐变 起始颜色
        Color onColorStart = new Color(Integer.parseInt("FF96A0", 16));
        //二维码前景渐变 结束颜色
        Color onColorEnd = new Color(Integer.parseInt("FF69B4", 16));
        //中心logo
        String logoUrl = "https://i1.hdslb.com/bfs/face/d973f5f34a9b102a9a0d6f216f806c225d66912f.jpg";
        //二维码内容
        String context = "https://github.com/MikuNyanya/Rabbit_QRCode";
//        String context = "https://tvax2.sinaimg.cn//large/ec43126fgy1gt6b4v977uj20wv1ctjys.jpg";
        //二维码输出路径
        String outPutPath = "E:\\";

        try {
            //生成一个普通黑白二维码
//            BufferedImage qrImg = QRCodeService.createQRCode(context);
            //生成一个彩色二维码
//            qrImg = QRCodeService.createGradientColorQRCode(context, onColor, offColor);
            //生成一个自定义尺寸，中心带logo的彩色二维码
//            qrImg = QRCodeService.createQRCode(context, width, height, onColor, offColor, logoUrl);

            //生成一个渐变色二维码
//            qrImg = QRCodeService.createGradientColorQRCode(context, onColorStart, onColorEnd);
            //生成一个自定义尺寸，中心带logo的渐变色二维码
//            qrImg = QRCodeService.createGradientColorQRCode(context, width, height, onColorStart, onColorEnd, offColor, logoUrl);

            //生成本地文件
//            ImageIO.write(qrImg, "png", new File(outPutPath+"qrImage.png"));




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


            VisualQRCodeUtil.createQRCode(context, "E:\\testqr2.jpg", outPutPath+"VisualQRCode12.png", 'Q',
                    new Color(49, 149, 0, 255), null, null, null, true,
//                    VisualQRCodeUtil.POSITION_DETECTION_SHAPE_MODEL_RECTANGLE, VisualQRCodeUtil.FILL_SHAPE_MODEL_CIRCLE);
                    VisualQRCodeUtil.POSITION_DETECTION_SHAPE_MODEL_ROUND_RECTANGLE, VisualQRCodeUtil.FILL_SHAPE_MODEL_CIRCLE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
