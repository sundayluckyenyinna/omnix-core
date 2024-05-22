package com.accionmfb.omnix.core.util.qrcode;

import com.accionmfb.omnix.core.commons.StringValues;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class QrCodeUtility {

    public static String generateQRCode(String text, String filePath, int width, int height){
        try {
            com.google.zxing.EncodeHintType hintType = EncodeHintType.CHARACTER_SET;
            String charset = StandardCharsets.UTF_8.toString();
            java.util.Map<com.google.zxing.EncodeHintType, String> hintMap = new java.util.HashMap<>();
            hintMap.put(hintType, charset);

            com.google.zxing.Writer writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outStream);
            File directory = new File(StringValues.DOT);
            String qrCodeAbsPath = directory.getAbsolutePath().concat(File.separator).concat(filePath);
            try (FileOutputStream fileOutStream = new FileOutputStream(qrCodeAbsPath)) {
                outStream.writeTo(fileOutStream);
            }
            return qrCodeAbsPath;
        }catch (Exception exception){
            return StringValues.EMPTY_STRING;
        }
    }

    public static String generateQRCode(String text, String filePath) {
        return generateQRCode(text, filePath, 500, 500);
    }
}
