package com.accionmfb.omnix.core.util.pdf;

import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.util.FileUtilities;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

@Slf4j
public class ItextPdfUtility implements PdfUtility{
    @Override
    public String convertToPdf(String htmlString, String pdfDocName) {
        return convertToStaticPdf(htmlString, pdfDocName);
    }

    public static String convertToStaticPdf(String htmlString, String pdfDocName){
        String folderAbsPath = FileUtilities.getDocumentFolderAbsPath();
        String pdfAbsPath = folderAbsPath.concat(File.separator).concat(pdfDocName);
        try{
            HtmlConverter.convertToPdf(htmlString, new FileOutputStream(pdfAbsPath));
            return pdfAbsPath;
        }catch (Exception exception){
            log.error("Exception occurred while trying to convert html to pdf. Exception message is: {}", exception.getMessage());
            exception.printStackTrace();
            return StringValues.EMPTY_STRING;
        }
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
}
