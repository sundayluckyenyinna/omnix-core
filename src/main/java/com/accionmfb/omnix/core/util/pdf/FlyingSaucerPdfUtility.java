package com.accionmfb.omnix.core.util.pdf;

import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.util.FileUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

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
        String folderAbsPath = FileUtilities.getDocumentFolderAbsPath();
        String pdfAbsPath = folderAbsPath.concat(File.separator).concat(pdfDocName);
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

    public static String convertToStaticPdf(String resourcePath, Map<String, Object> context, String pdfDocName){
        String htmlString = FileUtilities.downloadAndFormatOuterHtmlFromResource(resourcePath, context);
        return convertToStaticPdf(htmlString, pdfDocName);
    }

    public static String downloadAndConvertHtmlToPdfFromLink(String link, Map<String, Object> contextData, String pdfDocName){
        String htmlString = FileUtilities.downloadOuterHtmlFromHtmlLink(link);
        String formattedHtmlString = FileUtilities.formatHtmlLinkWithSimpleContextBinder(htmlString, contextData);
        return convertToStaticPdf(formattedHtmlString, pdfDocName);
    }

    private static String htmlToXhtml(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }
}
