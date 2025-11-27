package com.yapily.e2eqa4j.test_suite_runner.test_case;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestLIVEData
{
    public static final Map<String, Map<String, String>> stepNamesThatHaveExecuted;

    static
    {
        stepNamesThatHaveExecuted = new ConcurrentHashMap<>();
    }
}
