package com.ichat.command;

import com.ichat.common.Util;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCommand {
    protected Map<Character ,String> parameterMap;
    private String HELP_HTML_TEMPLATE;
    private String HELP_TEXT_TEMPLATE;

    private Util util = new Util();

    public AbstractCommand() {
        loadTemplates();
        initializeParams();
    }

    protected void initializeParams() {
        parameterMap = new HashMap<>();
        parameterMap.put(Command.HELP_PARAM, buildHelpText(Command.HELP_PARAM, "help", ""));
    }

    public Map<Character, String> getParameterMap() {
        return parameterMap;
    }

    public AbstractCommand setParameterMap(Map<Character, String> parameterMap) {
        this.parameterMap = parameterMap;
        return this;
    }

    public AbstractCommand addParameter(Character paramName, String paramDescription) {
        if (paramName == null || StringUtils.isEmpty(paramDescription)) {
            throw new IllegalArgumentException("Parameter name and argument must be non-empty!");
        }
        parameterMap.put(paramName, paramDescription);
        return this;
    }

    public abstract String getDescription();

    public Set<Character> getParameters() {
        return getParameterMap().keySet();
    }

    public String help() {
        StringBuilder helpRowBuilder = new StringBuilder();
        if (MapUtils.isNotEmpty(getParameterMap())) {
            getParameterMap().values().forEach(v -> helpRowBuilder.append(v));
        }
        return String.format(HELP_HTML_TEMPLATE, getDescription(), helpRowBuilder.toString());
    }

    private void loadTemplates() {
        try {
            HELP_HTML_TEMPLATE = util.getResourceFileContent("weather/HelpTemplate.txt");
            HELP_TEXT_TEMPLATE = util.getResourceFileContent("weather/HelpRow.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String buildHelpText(Character paramName, String paramValues, String paramDescription) {
       return String.format(HELP_TEXT_TEMPLATE, "--" + paramName.toString() + "=", paramValues, paramDescription);
    }
}
