package com.accionmfb.omnix.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String formatDate(LocalDateTime localDateTime, DateTimeFormatter formatter){
        return formatter.format(localDateTime);
    }

    public static String formatDate(LocalDateTime localDateTime, String pattern){
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    public static String formatDate(LocalDate localDate, DateTimeFormatter formatter){
        return formatter.format(localDate);
    }

    public static String formatDate(LocalDate localDate, String pattern){
        return DateTimeFormatter.ofPattern(pattern).format(localDate);
    }

    public static LocalDateTime fromDateTimeStringAndPattern(String dateString, String pattern){
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate fromDateStringAndPattern(String dateString, String pattern){
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern));
    }

    public static void main(String[] args) {
        System.out.println(DateUtils.formatDate(LocalDateTime.now(), "dd MMMM yyyy | HH:mm a"));
    }
}
