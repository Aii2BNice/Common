package com.dyq.demo.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.core.io.Resource;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtil {

	/** 二维码宽 */
	private static final int QRCODE_WIDTH = 146;
	/** 二维码高 */
	private static final int QRCODE_HEIGHT = 146;
	/** 黑色 */
	private static final int BLACK = 0xFF000000;
	/** 白色 */
	private static final int WHITE = 0xFFFFFFFF;
	
	/**
	 * 二维码生成
	 * @param content 二维码内容
	 * @param out 输出流
	 * @param printLogoFlg 是否包含Logo
	 * @throws Exception
	 */
	public static void createQRCode(String content, OutputStream out, boolean printLogoFlg) throws Exception {
		Map<EncodeHintType, Object> hints = new HashMap<>();
		// 字符集编码
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		// 纠错等级
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		//二维码大小
		hints.put(EncodeHintType.MARGIN, 2);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_WIDTH, QRCODE_HEIGHT, hints);
		if (printLogoFlg) {
			// 读取Logo
			Resource resource = ApplicationContextRegister.getApplicationContext().getResource("classpath:static/img/logo.png");
			if (resource != null) {
				BufferedImage qrcode = toBufferedImage(bitMatrix);
				Image logo = ImageIO.read(resource.getFile());
				Graphics2D graphics = qrcode.createGraphics();
				graphics.drawImage(logo, 54, 54, 40, 40, null);
				graphics.dispose();
				qrcode.flush();
				ImageIO.write(qrcode, "png", out);
			}
		} else {
			MatrixToImageWriter.writeToStream(bitMatrix, "png", out);
		}
	}
	
	/**
	 * 二维码读取
	 * @param image 二维码文件
	 * @return
	 */
	public static String readerQRCode(File image) {
		String content = "";
		BufferedImage reader = null;
		try {
			reader = ImageIO.read(image);
			LuminanceSource source = new BufferedImageLuminanceSource(reader);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap bitmap = new BinaryBitmap(binarizer);
			Map<DecodeHintType, Object> hints = new HashMap<>();
			hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			Result result = new MultiFormatReader().decode(bitmap, hints);
			if (result != null) {
				content = result.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	/**
	 * BitMatrix转BufferedImage
	 * @param matrix
	 * @return
	 */
	public static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}
	
}
