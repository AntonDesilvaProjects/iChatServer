package com.ichat.command;

import java.util.Map;
import java.util.Set;

public interface Command<T> {
    T process(Map<String, String> paramArgMap);
    String help();
    Set<Character> getParameters();
}
