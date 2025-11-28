package com.yapily.e2eqa4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestLIVEData
{
    public static final Map<String, Map<String, String>> stepNamesThatHaveExecuted;
    public static final Map<String, Map<String, String>> executorStepNamesThatHaveExecuted;
    public static final Map<String, String> globalVariables;

    static
    {
        stepNamesThatHaveExecuted = new ConcurrentHashMap<>();
        executorStepNamesThatHaveExecuted = new ConcurrentHashMap<>();
        globalVariables = new ConcurrentHashMap<>();
    }
}
