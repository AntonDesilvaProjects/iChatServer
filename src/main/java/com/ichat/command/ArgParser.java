package com.ichat.command;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgParser {
    public static Map<Character, String> getParametersAndArguments(String argParamStr, Set<Character> validParameters) {
        Map<Character, String> paramArgMap = new HashMap<>();
        if (StringUtils.isNotEmpty(argParamStr) && CollectionUtils.isNotEmpty(validParameters)) {
            Pattern pattern = Pattern.compile("(--[a-zA-Z]=.*?(?=--|$))|(--[a-zA-Z])", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(argParamStr);
            while (matcher.find()) {
                String paramArg = matcher.group();
                //the third character will always be the parameter identifier
                char param = paramArg.charAt(2);
                if (validParameters.contains(param)) {
                    String arg = paramArg.indexOf('=') < 0 ? null : paramArg.substring(paramArg.indexOf('=')+1);
                    arg = arg == null ? arg : arg.trim();
                    paramArgMap.put(param, arg); //everything after the equal sign is the argument
                }
            }
        }
        return paramArgMap;
    }
}
