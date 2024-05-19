package com.accionmfb.omnix.core.util.pdf;

import com.accionmfb.omnix.core.commons.StringValues;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenPdfUtility implements PdfUtility{

    @Override
    public String convertToPdf(String htmlString, String pdfDocName) {
        return OpenPdfUtility.convertToStaticPdf(htmlString, pdfDocName);
    }

    public static String convertToStaticPdf(String htmlString, String pdfDocName){
        Document doc = Jsoup.parse(htmlString, Strings.EMPTY, Parser.htmlParser());
        File currentFolder = new File(StringValues.DOT);
        String folderAbsPath = currentFolder.getAbsolutePath();
        String pdfAbsPath = folderAbsPath.concat(pdfDocName);
        try (OutputStream os = new FileOutputStream(pdfAbsPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withUri(pdfAbsPath);
            builder.useFastMode();
            builder.useSVGDrawer(new BatikSVGDrawer());
            builder.toStream(os);
            builder.withW3cDocument(new W3CDom().fromJsoup(doc), "/");
            builder.run();
        }catch (Exception exception){
            log.error("Exception message: {}", exception.getMessage());
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return pdfAbsPath;
    }
}
