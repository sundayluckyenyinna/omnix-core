package com.accionmfb.omnix.core.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Map;

@Slf4j
public class FileUtilities {

    private static final TemplateEngine TEMPLATE_ENGINE = new SpringTemplateEngine();
    private static final String DEFAULT_THYMELEAF_PREFIX_PLACEHOLDER = "\\[[{";
    private static final String DEFAULT_THYMELEAF_SUFFIX_PLACEHOLDER = "}]]";

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
            result = result.replaceAll(replaceAble, String.valueOf(value));
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
}
