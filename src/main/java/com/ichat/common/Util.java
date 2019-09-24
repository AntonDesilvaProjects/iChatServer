package com.ichat.common;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {
    public static Integer tryParseInt(String number) {
        Integer result = null;
        try {
            result = Integer.parseInt(number);
        } catch (NumberFormatException n) {
            //do nothing
        }
        return result;
    }

    public static String convertTime(long time, String formatString){
        Date date = new Date(TimeUnit.SECONDS.toMillis(time));
        Format format = new SimpleDateFormat(formatString);
        return format.format(date);
    }

    public String getResourceFileContent(String filePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
