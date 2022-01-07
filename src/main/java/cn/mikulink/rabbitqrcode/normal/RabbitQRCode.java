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
        int[] alignmentPatternCenters = qrCode.getVersion().getAlignmentPatternCenters();


        //二维码长宽
        int qrCodeSize = qrCodeConfig.getWidth();
        //二维码白边 可设置范围为0-10，但仅四个变化0 1(2) 3(4 5 6) 7(8 9 10)
        int quietZone = 1;
        // 创建矩阵对象
        BitMatrix bitMatrix = renderResult(qrCode, qrCodeSize, qrCodeSize, quietZone);

        //转化为二维码图片对象
        BufferedImage bufferedImage = toBufferedImage(bitMatrix, qrCodeConfig, alignmentPatternCenters);


        //背景图
        if (null != qrCodeConfig.getBgImgPath()) {
            BufferedImage bgImg = ImageIO.read(new File(qrCodeConfig.getBgImgPath()));

            //需要是3的倍数
            int scale = 3;

            int[][] pattern = new int[bufferedImage.getWidth() - scale * 4 * 2][bufferedImage.getWidth() - scale * 4 * 2];

            //根据纠错等级，标记空白处？
            for (int alignmentPatternCenter : alignmentPatternCenters) {
                for (int patternCenter : alignmentPatternCenters) {
                    if (alignmentPatternCenter == 6 && patternCenter == alignmentPatternCenters[alignmentPatternCenters.length - 1] ||
                            (patternCenter == 6 && alignmentPatternCenter == alignmentPatternCenters[alignmentPatternCenters.length - 1]) ||
                            (alignmentPatternCenter == 6 && patternCenter == 6)) {
                        continue;
                    } else {
                        int initx = scale * (alignmentPatternCenter - 2);
                        int inity = scale * (patternCenter - 2);
                        for (int x = initx; x < initx + scale * 5; x++) {
                            for (int y = inity; y < inity + scale * 5; y++) {
                                pattern[x][y] = 1;
                            }
                        }
                    }
                }
            }

            int imageSize = bgImg.getWidth();

            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    if ((i * 3 / scale) % 3 == 1 && (j * 3 / scale) % 3 == 1) {
                        continue;
                    }
                    if (i < scale * 4 * 2 && (j < scale * 4 * 2 || j > imageSize - (scale * 4 * 2 + 1))) {
                        continue;
                    }
                    if (i > imageSize - (scale * 4 * 2 + 1) && j < scale * 4 * 2) {
                        continue;
                    }

                    if(pattern.length<=i ||pattern[i].length<=j){
                        bufferedImage.setRGB(i + scale * 4, j + scale * 4, bgImg.getRGB(i, j));
                        continue;
                    }
                    if (pattern[i][j] != 1) {
                        continue;
                    }

//                    scaledQRImage.setPixel(i + scale * 4, j + scale * 4, blackWhite.getPixel(i, j));
                    bufferedImage.setRGB(i + scale * 4, j + scale * 4, bgImg.getRGB(i, j));
//                    bufferedImage.setRGB(i + scale * 4,j + scale * 4,bgImg.getRGB(i,j), y, width, 1, rowPixels, 0, width);
                }

            }

            bufferedImage = bgImg;
        }

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
    private static BufferedImage toBufferedImage(BitMatrix matrix, RabbitQRCodeConfig config, int[] alignmentPatternCenters) throws IOException {
//        int scale = 2;

        int width = matrix.getWidth();
        int height = matrix.getHeight();
//        int width = matrix.getWidth() * scale;
//        int height = matrix.getHeight() * scale;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int colorType = config.getColorType();
        Color onColor = config.getOnColor();
        Color offColor = config.getOnColor();
        Color bgColor = config.getBgColor();
        Color gradientColorStart = config.getGradientColorStart();
        Color gradientColorEnd = config.getGradientColorEnd();
//        BufferedImage bgImg = null;
//        if (null != config.getBgImgPath()) {
//            bgImg = ImageIO.read(new File(config.getBgImgPath()));
//        }

        int[] rowPixels = new int[width];
        BitArray row = new BitArray(width);
//        int[] rowPixels = new int[width * scale];
//        BitArray row = new BitArray(width * scale);

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
//            if (y % scale != 0) {
//                continue;
//            }
            row = matrix.getRow(y, row);
//            row = matrix.getRow(y / scale, row);

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
                //此处区分绘制二维码还是绘制背景
//                if (x % scale != 0) {
//                    continue;
//                }
//                if (null != bgImg) {
//                    //画图片，并设置透明
//                    int imagergbTemp = bgImg.getRGB(x, y);
//                    Color colorTemp = new Color(imagergbTemp);
//                    colorTemp = new Color(colorTemp.getRed(),colorTemp.getGreen(),colorTemp.getBlue(),200);
//                    rowPixels[x] = row.get(x) ? onColorTemp : colorTemp.getRGB();
//                } else {
                    rowPixels[x] = row.get(x) ? onColorTemp : bgColor.getRGB();
//                }
//                rowPixels[x] = row.get(x / scale) ? onColorTemp : bgColor.getRGB();
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
