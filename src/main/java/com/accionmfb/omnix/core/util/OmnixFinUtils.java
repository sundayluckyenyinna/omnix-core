package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.StringValues;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.CurrencyAmount;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;

public class OmnixFinUtils {

    private static final int FIN_DECIMAL_PLACE = 2;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");

    public static BigDecimal ofFinString(String finance){
        finance = finance.replaceAll(StringValues.COMMA, StringValues.EMPTY_STRING).trim();
        return setFinScale(new BigDecimal(finance));
    }

    public static BigDecimal ofFinDouble(double finance){
        return setFinScale(BigDecimal.valueOf(finance));
    }

    public static BigDecimal addFin(String val1, String val2){
        return setFinScale(ofFinString(val1).add(ofFinString(val2)));
    }

    public static BigDecimal addFin(String val1, double val2){
        return setFinScale(ofFinString(val1).add(ofFinDouble(val2)));
    }

    public static BigDecimal addFin(double val1, double val2){
        return setFinScale(ofFinDouble(val1).add(ofFinDouble(val2)));
    }

    public static BigDecimal subtractFin(String val1, String val2){
        return setFinScale(ofFinString(val1).subtract(ofFinString(val2)));
    }

    public static BigDecimal subtractFin(double val1, double val2){
        return setFinScale(ofFinDouble(val1).subtract(ofFinDouble(val2)));
    }

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor){
        BigDecimal scaledDividend = setFinScale(dividend);
        BigDecimal scaledDivisor = setFinScale(divisor);
        return scaledDividend.divide(scaledDivisor, FIN_DECIMAL_PLACE, RoundingMode.CEILING);
    }

    public static BigDecimal divide(String dividend, String divisor){
        return divide(ofFinString(dividend), ofFinString(divisor));
    }

    public static BigDecimal divide(BigDecimal dividend, double divisor){
        return divide(dividend, ofFinDouble(divisor));
    }

    public static BigDecimal divide(BigDecimal dividend, Integer divisor){
        return divide(dividend, ofFinDouble(Double.parseDouble(String.valueOf(divisor))));
    }

    public static BigDecimal setFinScale(BigDecimal bigDecimal){
        return bigDecimal.setScale(FIN_DECIMAL_PLACE, RoundingMode.CEILING);
    }

    public static String formatFin(BigDecimal bigDecimal){
        return DECIMAL_FORMAT.format(bigDecimal);
    }

    public static String formatFin(String value){
        return formatFin(ofFinString(value));
    }

    public static BigDecimal finPercentChange(String percent, BigDecimal amount){
        BigDecimal change = OmnixFinUtils.ofFinString(percent)
                .divide(OmnixFinUtils.ofFinDouble(100), FIN_DECIMAL_PLACE, RoundingMode.CEILING)
                .multiply(amount);
        return change.setScale(FIN_DECIMAL_PLACE, RoundingMode.CEILING);
    }

    public static String translate(String countryCode, String lang, String amount, String fractionUnitName) {
        try {
            StringBuilder result = new StringBuilder();
            Locale locale = new Locale(lang, countryCode);
            Currency currency = Currency.getInstance(locale);
            String[] inputArr = StringUtils.split(ofFinString(amount).abs().toPlainString(), StringValues.DOT);
            RuleBasedNumberFormat rule = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);

            int i = 0;
            for (String input : inputArr) {
                CurrencyAmount currencyAmount = new CurrencyAmount(new BigDecimal(input), currency);
                if (i++ == 0) {
                    result.append(rule.format(currencyAmount)).append(StringValues.EMPTY_STRING).append(currency.getDisplayName()).append(" and ");
                } else {
                    result.append(rule.format(currencyAmount)).append(StringValues.SINGLE_SPACE).append(fractionUnitName).append(StringValues.SINGLE_SPACE);
                }
            }
            String finalResult = result.toString().replace("Nigerian", StringValues.EMPTY_STRING);
            if(finalResult.endsWith("and")){
                finalResult = finalResult.substring(0, finalResult.lastIndexOf("and")).trim();
            }
            return String.valueOf(finalResult.charAt(0)).toUpperCase().concat(finalResult.substring(1).toLowerCase());
        }catch (Exception exception){
            return StringValues.EMPTY_STRING;
        }
    }

    public static String translate(String amount){
        String countryCode = "NG";
        String language = "en";
        String fractionUnitName = "kobo";
        return translate(countryCode, language, amount, fractionUnitName);
    }

    public static String translate(BigDecimal bigDecimal){
        return translate(bigDecimal.toString());
    }

    public static String translate(double value){
        return translate(ofFinDouble(value));
    }
}
