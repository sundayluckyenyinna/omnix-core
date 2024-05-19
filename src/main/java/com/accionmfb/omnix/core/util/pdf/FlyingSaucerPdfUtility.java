package com.accionmfb.omnix.core.util.pdf;

import com.accionmfb.omnix.core.commons.StringValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlyingSaucerPdfUtility implements PdfUtility{

    @Override
    public String convertToPdf(String htmlString, String pdfDocName) {
        return null;
    }

    public static String convertToStaticPdf(String htmlString, String pdfDocName){
        String xhtml = htmlToXhtml(htmlString);
        File currentFolder = new File(StringValues.DOT);
        String folderAbsPath = currentFolder.getAbsolutePath();
        String pdfAbsPath = folderAbsPath.concat(pdfDocName);
        try{
            ITextRenderer iTextRenderer = new ITextRenderer();
            iTextRenderer.setDocumentFromString(xhtml);
            iTextRenderer.layout();
            OutputStream os = new FileOutputStream(pdfAbsPath);
            iTextRenderer.createPDF(os);
            os.close();
        }catch (Exception exception){
            log.error("Exception message: {}", exception.getMessage());
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return pdfAbsPath;
    }

    private static String htmlToXhtml(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }
}
