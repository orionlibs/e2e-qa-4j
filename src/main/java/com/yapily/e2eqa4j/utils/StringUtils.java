package com.yapily.e2eqa4j.utils;

import com.yapily.e2eqa4j.test_suite_runner.test_case.TestLIVEData;
import java.util.Map;
import java.util.Map.Entry;

public class StringUtils
{
    public static String stripQuotes(String s)
    {
        if(s == null)
        {
            return null;
        }
        s = s.trim();
        if((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
        {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }


    public static void injectValue(Map.Entry<String, String> entry, String placeholderToReplace, String replacement)
    {
        if(placeholderToReplace.indexOf("{") == -1 && placeholderToReplace.indexOf("}") == -1)
        {
            placeholderToReplace = "{{" + placeholderToReplace + "}}";
        }
        String result = entry.getValue().replace(placeholderToReplace, replacement);
        entry.setValue(result);
    }


    public static String injectValue(String stringToInjectTo, String placeholderToReplace, String replacement)
    {
        if(placeholderToReplace.indexOf("{") == -1 && placeholderToReplace.indexOf("}") == -1)
        {
            placeholderToReplace = "{{" + placeholderToReplace + "}}";
        }
        return stringToInjectTo.replace(placeholderToReplace, replacement);
    }


    public static void processReplacementsInTestStepUsingStepsAlreadyExecuted(String[] keyParts, Map.Entry<String, String> entry)
    {
        for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.stepNamesThatHaveExecuted.entrySet())
        {
            if(stepThatHasExecuted.getKey().equals(keyParts[0]))
            {
                injectValue(entry, entry.getValue(), stepThatHasExecuted.getValue().get(keyParts[1]));
            }
        }
    }


    public static String processReplacementsInTestStepUsingStepsAlreadyExecuted(String[] keyParts, String entry, String placeholder)
    {
        for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.stepNamesThatHaveExecuted.entrySet())
        {
            if(keyParts.length == 2 && stepThatHasExecuted.getKey().equals(keyParts[0].substring(2)))
            {
                entry = StringUtils.injectValue(entry, placeholder, stepThatHasExecuted.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2)));
            }
        }
        return entry;
    }


    public static void processReplacementsInTestStepUsingStepsAlreadyExecuted(String[] keyParts, Map.Entry<String, String> entry, String placeholder)
    {
        for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.stepNamesThatHaveExecuted.entrySet())
        {
            if(keyParts.length == 2 && stepThatHasExecuted.getKey().equals(keyParts[0].substring(2)))
            {
                StringUtils.injectValue(entry, placeholder, stepThatHasExecuted.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2)));
            }
        }
    }
}
