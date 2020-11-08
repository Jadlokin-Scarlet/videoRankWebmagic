package com.tilitili.spider.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class Log {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String getName() {
        return log.getName();
    }

    public static boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    public static void trace(String msg) {
        log.trace(msg);
    }

    public static void trace(String format, Object arg) {
        log.trace(format, gson.toJson(arg));
    }

    public static void trace(String format, Object arg1, Object arg2) {
        log.trace(format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void trace(String format, Object... arguments) {
        log.trace(format, Arrays.stream(arguments).map(gson::toJson).collect(Collectors.toList()));
    }

    public static void trace(String msg, Throwable t) {
        log.trace(msg, t);
    }

    public static boolean isTraceEnabled(Marker marker) {
        return log.isTraceEnabled(marker);
    }

    public static void trace(Marker marker, String msg) {
        log.trace(marker, msg);
    }

    public static void trace(Marker marker, String format, Object arg) {
        log.trace(marker, format, gson.toJson(arg));
    }

    public static void trace(Marker marker, String format, Object arg1, Object arg2) {
        log.trace(marker, format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void trace(Marker marker, String format, Object... argArray) {
        log.trace(marker, format, argArray);
    }

    public static void trace(Marker marker, String msg, Throwable t) {
        log.trace(marker, msg, t);
    }

    public static boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public static void debug(String msg) {
        log.debug(msg);
    }

    public static void debug(String format, Object arg) {
        log.debug(format, gson.toJson(arg));
    }

    public static void debug(String format, Object arg1, Object arg2) {
        log.debug(format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void debug(String format, Object... arguments) {
        log.debug(format, Arrays.stream(arguments).map(gson::toJson).collect(Collectors.toList()));
    }

    public static void debug(String msg, Throwable t) {
        log.debug(msg, t);
    }

    public static boolean isDebugEnabled(Marker marker) {
        return log.isDebugEnabled(marker);
    }

    public static void debug(Marker marker, String msg) {
        log.debug(marker, msg);
    }

    public static void debug(Marker marker, String format, Object arg) {
        log.debug(marker, format, gson.toJson(arg));
    }

    public static void debug(Marker marker, String format, Object arg1, Object arg2) {
        log.debug(marker, format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void debug(Marker marker, String format, Object... arguments) {
        log.debug(marker, format, Arrays.stream(arguments).map(gson::toJson).collect(Collectors.toList()));
    }

    public static void debug(Marker marker, String msg, Throwable t) {
        log.debug(marker, msg, t);
    }

    public static boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public static void info(String msg) {
        log.info(msg);
    }

    public static void info(String format, Object arg) {
        log.info(format, gson.toJson(arg));
    }

    public static void info(String format, Object arg1, Object arg2) {
        log.info(format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void info(String format, Object... arguments) {
        log.info(format, Arrays.stream(arguments).map(gson::toJson).collect(Collectors.toList()));
    }

    public static void info(String msg, Throwable t) {
        log.info(msg, t);
    }

    public static boolean isInfoEnabled(Marker marker) {
        return log.isInfoEnabled(marker);
    }

    public static void info(Marker marker, String msg) {
        log.info(marker, msg);
    }

    public static void info(Marker marker, String format, Object arg) {
        log.info(marker, format, gson.toJson(arg));
    }

    public static void info(Marker marker, String format, Object arg1, Object arg2) {
        log.info(marker, format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void info(Marker marker, String format, Object... arguments) {
        log.info(marker, format, Arrays.stream(arguments).map(gson::toJson).collect(Collectors.toList()));
    }

    public static void info(Marker marker, String msg, Throwable t) {
        log.info(marker, msg, t);
    }

    public static boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    public static void warn(String msg) {
        log.warn(msg);
    }

    public static void warn(String format, Object arg) {
        log.warn(format, gson.toJson(arg));
    }

    public static void warn(String format, Object... arguments) {
        log.warn(format, Arrays.stream(arguments).map(gson::toJson).collect(Collectors.toList()));
    }

    public static void warn(String format, Object arg1, Object arg2) {
        log.warn(format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void warn(String msg, Throwable t) {
        log.warn(msg, t);
    }

    public static boolean isWarnEnabled(Marker marker) {
        return log.isWarnEnabled(marker);
    }

    public static void warn(Marker marker, String msg) {
        log.warn(marker, msg);
    }

    public static void warn(Marker marker, String format, Object arg) {
        log.warn(marker, format, gson.toJson(arg));
    }

    public static void warn(Marker marker, String format, Object arg1, Object arg2) {
        log.warn(marker, format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void warn(Marker marker, String format, Object... arguments) {
        log.warn(marker, format, Arrays.stream(arguments).map(gson::toJson).collect(Collectors.toList()));
    }

    public static void warn(Marker marker, String msg, Throwable t) {
        log.warn(marker, msg, t);
    }

    public static boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public static void error(String msg) {
        log.error(msg);
    }

    public static void error(String format, Object arg) {
        log.error(format, gson.toJson(arg));
    }

    public static void error(String format, Object arg1, Object arg2) {
        log.error(format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void error(String format, Object... arguments) {
        log.error(format, Arrays.stream(arguments).map(gson::toJson).collect(Collectors.toList()));
    }

    public static void error(String msg, Throwable t) {
        log.error(msg, t);
    }

    public static boolean isErrorEnabled(Marker marker) {
        return log.isErrorEnabled(marker);
    }

    public static void error(Marker marker, String msg) {
        log.error(marker, msg);
    }

    public static void error(Marker marker, String format, Object arg) {
        log.error(marker, format, gson.toJson(arg));
    }

    public static void error(Marker marker, String format, Object arg1, Object arg2) {
        log.error(marker, format, gson.toJson(arg1), gson.toJson(arg2));
    }

    public static void error(Marker marker, String format, Object... arguments) {
        log.error(marker, format, Arrays.stream(arguments).map(gson::toJson).collect(Collectors.toList()));
    }

    public static void error(Marker marker, String msg, Throwable t) {
        log.error(marker, msg, t);
    }
}
