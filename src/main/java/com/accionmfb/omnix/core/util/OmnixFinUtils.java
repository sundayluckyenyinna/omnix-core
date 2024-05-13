package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.StringValues;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OmnixFinUtils {

    private static final int FIN_DECIMAL_PLACE = 2;

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

    public static BigDecimal setFinScale(BigDecimal bigDecimal){
        return bigDecimal.setScale(FIN_DECIMAL_PLACE, RoundingMode.HALF_UP);
    }

    public static void main(String[] args) {
        System.out.println(ofFinString("3,000,000"));
    }
}
