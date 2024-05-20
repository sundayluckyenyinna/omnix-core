package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.StringValues;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class FileUtilities {

    private static final TemplateEngine TEMPLATE_ENGINE = new SpringTemplateEngine();
    private static final String DEFAULT_THYMELEAF_PREFIX_PLACEHOLDER = "[[${";
    private static final String DEFAULT_THYMELEAF_SUFFIX_PLACEHOLDER = "}]]";
    private static final String SIMPLE_CONTEXT_START_TAG = "\\{";
    private static final String SIMPLE_CONTEXT_END_TAG = "}";


    // ------------------------ Streaming ------------------//
    public static String downloadOuterHtmlFromHtmlLink(String htmlLink){
        try {
            Document document = Jsoup.connect(htmlLink).parser(Parser.htmlParser()).get();
            return document.outerHtml();
        }catch (Exception exception){
            log.error("Could not download html link.");
            log.info("Exception message is: {}", exception.getMessage());
            return null;
        }
    }

    public static String downloadOuterXmlFromXmlLink(String xmlLink){
        try{
            return Jsoup.connect(xmlLink).parser(Parser.xmlParser()).get().outerHtml();
        }catch (Exception exception){
            log.error("Could not download html link.");
            log.info("Exception message is: {}", exception.getMessage());
            return null;
        }
    }

    public static String downloadOuterHmlFromInputStream(InputStream htmlStream, Parser parser){
        try{
            return Jsoup.parse(htmlStream, StandardCharsets.UTF_8.toString(), StringValues.EMPTY_STRING, parser).outerHtml();
        }catch (Exception exception){
            log.error("Could not download {} from stream.", parser.toString());
            log.info("Exception message is: {}", exception.getMessage());
            return null;
        }
    }

    public static String downloadOuterHmlFromInputStream(InputStream htmlStream){
        try{
            return Jsoup.parse(htmlStream, StandardCharsets.UTF_8.toString(), StringValues.EMPTY_STRING, Parser.htmlParser()).outerHtml();
        }catch (Exception exception){
            log.error("Could not download html from stream.");
            log.info("Exception message is: {}", exception.getMessage());
            return null;
        }
    }

    public static String downloadOuterHtmlFromResource(String resourcePath, Parser parser){
        try {
            Resource resource = new ClassPathResource(resourcePath);
            return downloadOuterHmlFromInputStream(resource.getInputStream(), parser);
        }catch (Exception exception){
            return null;
        }
    }

    public static String downloadOuterHtmlFromResource(String resourcePath){
        try{
            return downloadOuterHtmlFromResource(resourcePath, Parser.htmlParser());
        }catch (Exception exception){
            return null;
        }
    }

    // ---------------------- Formatting ------------------ //
    public static String formatHtmlWithContextToPlain(String outerHtml, Map<String, Object> model){
        Context context = new Context();
        model.forEach(context::setVariable);
        return TEMPLATE_ENGINE.process(outerHtml, context);
    }

    public static String formatHtmlWithContextObjectToPlain(String outerHtml, Object pojo){
        return formatHtmlWithContextToPlain(outerHtml, CommonUtil.pojoToMap(pojo));
    }

    public static String downloadFormattedOuterHmlToPlain(String htmlLink, Map<String, Object> context){
        String outerHtml = downloadOuterHtmlFromHtmlLink(htmlLink);
        return formatHtmlWithContextToPlain(outerHtml, context);
    }

    public static String formatHtmlLeaveTag(String outerHtml, Map<String, Object> context){
        String result = outerHtml;
        for(Map.Entry<String, Object> entry : context.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            String replaceAble = DEFAULT_THYMELEAF_PREFIX_PLACEHOLDER.concat(key).concat(DEFAULT_THYMELEAF_SUFFIX_PLACEHOLDER);
            result = result.replace(replaceAble, String.valueOf(value));
        }
        return result;
    }

    public static TemplateFormatDetails formatHmlLinkForDetails(String link, Map<String, Object> context){
        String outerHtml = downloadOuterHtmlFromHtmlLink(link);
        String formattedHml = formatHtmlLeaveTag(outerHtml, context);
        String plainMessage = formatHtmlWithContextToPlain(outerHtml, context);
        return TemplateFormatDetails.builder()
                .outerHtml(outerHtml)
                .formattedHtml(formattedHml)
                .plainMessage(plainMessage)
                .build();
    }

    public static String formatHtmlLinkWithSimpleContextBinder(String htmlOuterHtml, Map<String, Object> context){
        String parsedOuterHtml = htmlOuterHtml;
        for(Map.Entry<String, Object> entry: context.entrySet()){
            String replaceable = SIMPLE_CONTEXT_START_TAG.concat(entry.getKey()).concat(SIMPLE_CONTEXT_END_TAG);
            parsedOuterHtml = parsedOuterHtml.replaceAll(replaceable, String.valueOf(entry.getValue()));
        }
        return parsedOuterHtml;
    }


    // ------------- Advanced combination --------- //
    public static String downloadAndFormatOuterHtmlFromResource(String resourcePath, Parser parser, Map<String, Object> context){
        try{
            String htmlString = downloadOuterHtmlFromResource(resourcePath, parser);
            return formatHtmlLinkWithSimpleContextBinder(htmlString, context);
        }catch (Exception exception){
            return null;
        }
    }

    public static String downloadAndFormatOuterHtmlFromResource(String resourcePath, Map<String, Object> context){
        try{
            String htmlString = downloadOuterHtmlFromResource(resourcePath);
            return formatHtmlLinkWithSimpleContextBinder(htmlString, context);
        }catch (Exception exception){
            return null;
        }
    }

    public static String getDocumentFolderAbsPath(){
        File currentFolder = new File(StringValues.DOT);
        File documentFolder = new File(currentFolder, "documents");
        if(documentFolder.exists()){
            return documentFolder.getAbsolutePath();
        }
        boolean directoryCreated = documentFolder.mkdirs();
        System.out.printf("Directory created: %s with path: %s", directoryCreated, documentFolder.getAbsolutePath());
        return documentFolder.getAbsolutePath();
    }
}
