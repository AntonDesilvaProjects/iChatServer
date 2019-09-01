package com.ichat.command;

import java.util.Map;
import java.util.Set;

public interface Command<T> {
    Character HELP_PARAM = 'h';
    T process(Map<String, String> paramArgMap);
    String help();
    Set<Character> getParameters();
    Character getDefaultParameter();
}
