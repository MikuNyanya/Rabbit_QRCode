package cn.mikulink.rabbitqrcode.normal;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * created by MikuNyanya on 2022/1/5 11:20
 * For the Reisen
 */
public class RabbitQRCode {

    /**
     * 生成一个二维码图片
     *
     * @param content      二维码内容
     * @param qrCodeConfig 二维码设置
     * @return 二维码图片
     * @throws WriterException
     * @throws IOException
     */
    public static BufferedImage createQRCode(String content, RabbitQRCodeConfig qrCodeConfig) throws WriterException, IOException {
        //如果没有传入配置，则使用默认值配置
        if (null == qrCodeConfig) {
            qrCodeConfig = new RabbitQRCodeConfig();
        }

        // 二维码基本参数设置
        Map<EncodeHintType, Object> hints = new HashMap<>();
        // 设置编码字符集utf-8
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 设置纠错等级L/M/Q/H,纠错等级越高越不易识别，最高等级H
        hints.put(EncodeHintType.ERROR_CORRECTION, qrCodeConfig.getLevel());
        //白边 可设置范围为0-10，但仅四个变化0 1(2) 3(4 5 6) 7(8 9 10)
        hints.put(EncodeHintType.MARGIN, 0);
        // 创建二维码
        QRCode qrCode = Encoder.encode(content, qrCodeConfig.getLevel(), hints);

        //二维码长宽
        int qrCodeSize = qrCodeConfig.getWidth();
        //二维码白边 可设置范围为0-10，但仅四个变化0 1(2) 3(4 5 6) 7(8 9 10)
        int quietZone = 1;
        // 创建矩阵对象
        BitMatrix bitMatrix = renderResult(qrCode, qrCodeSize, qrCodeSize, quietZone);

        //转化为二维码图片对象
        BufferedImage bufferedImage = toBufferedImage(bitMatrix, qrCodeConfig);

        //绘制中心logo
        String logoUrl = qrCodeConfig.getLogoUrl();
        if (null != logoUrl && logoUrl.length() > 0) {
            //从网络连接读取图片
            URL url = new URL(logoUrl);
            InputStream inputStream = url.openStream();
            BufferedImage logoImg = ImageIO.read(inputStream);
            //绘制logo
            qrLogoDraw(bufferedImage, logoImg, qrCodeConfig.getBgColor());
        }

        return bufferedImage;
    }

    //google zxing的源码，由于私有无法外部调用所以copy一份出来方便调用
    private static BitMatrix renderResult(QRCode code, int width, int height, int quietZone) {
        ByteMatrix input;
        if ((input = code.getMatrix()) == null) {
            throw new IllegalStateException();
        } else {
            int inputWidth = input.getWidth();
            int inputHeight = input.getHeight();
            int qrWidth = inputWidth + (quietZone << 1);
            int qrHeight = inputHeight + (quietZone << 1);
            int outputWidth = Math.max(width, qrWidth);
            int outputHeight = Math.max(height, qrHeight);
            int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
            int leftPadding = (outputWidth - inputWidth * multiple) / 2;
            int topPadding = (outputHeight - inputHeight * multiple) / 2;
            BitMatrix output = new BitMatrix(outputWidth, outputHeight);
            int inputY = 0;

            for (int outputY = topPadding; inputY < inputHeight; outputY += multiple) {
                int inputX = 0;

                for (int outputX = leftPadding; inputX < inputWidth; outputX += multiple) {
                    if (input.get(inputX, inputY) == 1) {
                        output.setRegion(outputX, outputY, multiple, multiple);
                    }

                    ++inputX;
                }

                ++inputY;
            }

            return output;
        }
    }

    //矩阵图片转为BufferedImage
    private static BufferedImage toBufferedImage(BitMatrix matrix, RabbitQRCodeConfig config) throws IOException {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int colorType = config.getColorType();
        Color onColor = config.getOnColor();
        Color offColor = config.getOnColor();
        Color bgColor = config.getBgColor();
        Color gradientColorStart = config.getGradientColorStart();
        Color gradientColorEnd = config.getGradientColorEnd();

        int[] rowPixels = new int[width];
        BitArray row = new BitArray(width);

        //如果是渐变色，需要计算出从开始到结束，每行需要渐变的r g b数值，然后在每次循环时累加，以达到渐变效果
        double increaseR = 0.0;
        double increaseG = 0.0;
        double increaseB = 0.0;
        if (colorType == RabbitQRCodeConfig.QRCODE_COLOR_TYPE_GRADIENT) {
            int colorStartR = gradientColorStart.getRed();
            int colorStartG = gradientColorStart.getGreen();
            int colorStartB = gradientColorStart.getBlue();

            int colorEndR = gradientColorEnd.getRed();
            int colorEndG = gradientColorEnd.getGreen();
            int colorEndB = gradientColorEnd.getBlue();

            //以两个颜色的差值，除以行数，以算出每行之间渐变多少
            //先来个简单粗暴的除法试试水，如果两个颜色相近，颜色可能会出现小偏差
            increaseR = (colorStartR - colorEndR) / (height * 1.0);
            increaseG = (colorStartG - colorEndG) / (height * 1.0);
            increaseB = (colorStartB - colorEndB) / (height * 1.0);
        }

        for (int y = 0; y < height; ++y) {
            row = matrix.getRow(y, row);

            //渐变色需要先确定当前行使用什么颜色
            int onColorTemp = onColor.getRGB();
            if (colorType == RabbitQRCodeConfig.QRCODE_COLOR_TYPE_GRADIENT) {
                //偏移RGB
                int colorR = gradientColorStart.getRed() - ((int) (increaseR * y));
                if (colorR < 0) colorR = 0;
                if (colorR > 255) colorR = 255;
                int colorG = gradientColorStart.getGreen() - ((int) (increaseG * y));
                if (colorG < 0) colorG = 0;
                if (colorG > 255) colorG = 255;
                int colorB = gradientColorStart.getBlue() - ((int) (increaseB * y));
                if (colorB < 0) colorB = 0;
                if (colorB > 255) colorB = 255;

                Color gradientColor = new Color(colorR, colorG, colorB);
                onColorTemp = gradientColor.getRGB();
            }

            for (int x = 0; x < width; ++x) {
                rowPixels[x] = row.get(x) ? onColorTemp : bgColor.getRGB();
            }

            image.setRGB(0, y, width, 1, rowPixels, 0, width);
        }

        return image;
    }

    //绘制二维码中间的logo
    private static BufferedImage qrLogoDraw(BufferedImage qrImg, BufferedImage logoImg, Color bgColor) {
        //读取二维码图片，并构建绘图对象
        Graphics2D g2 = qrImg.createGraphics();

        int matrixWidth = qrImg.getWidth();
        int matrixHeigh = qrImg.getHeight();

        //开始绘制图片
        g2.drawImage(logoImg, matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, null);//绘制
        BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);// 设置笔画对象

        //开始绘制周围边框
        //从角落圆弧的宽度
        int arcw = 10;
        //从角落圆弧的高度
        int arch = 10;

        //默认为白色周边背景
        if (null == bgColor) {
            bgColor = RabbitQRCodeConfig.BGCOLOR_DEFAULT;
        }
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, arcw, arch);
        g2.setColor(bgColor);
        g2.draw(round);// 绘制圆弧矩形

        //绘制logo灰色线条边框
        BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth / 5 * 2 + 2, matrixHeigh / 5 * 2 + 2, matrixWidth / 5 - 4, matrixHeigh / 5 - 4, arcw, arch);
        g2.setColor(new Color(128, 128, 128));
        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        qrImg.flush();
        return qrImg;
    }
}
