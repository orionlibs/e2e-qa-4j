package com.yapily.e2eqa4j.utils;

import com.yapily.e2eqa4j.TestLIVEData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public static void processReplacementsInStepUsingStepsAlreadyExecuted(String[] keyParts, Map.Entry<String, String> entry)
    {
        for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.stepNamesThatHaveExecuted.entrySet())
        {
            if(stepThatHasExecuted.getKey().equals(keyParts[0]))
            {
                injectValue(entry, entry.getValue(), stepThatHasExecuted.getValue().get(keyParts[1]));
            }
        }
        for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.executorStepNamesThatHaveExecuted.entrySet())
        {
            if(stepThatHasExecuted.getKey().equals(keyParts[0]))
            {
                injectValue(entry, entry.getValue(), stepThatHasExecuted.getValue().get(keyParts[1]));
            }
        }
    }


    public static String processReplacementsInStepUsingStepsAlreadyExecuted(String[] keyParts, String entry, String placeholder)
    {
        for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.stepNamesThatHaveExecuted.entrySet())
        {
            if(keyParts.length == 2 && stepThatHasExecuted.getKey().equals(keyParts[0].substring(2)))
            {
                entry = StringUtils.injectValue(entry, placeholder, stepThatHasExecuted.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2)));
            }
        }
        for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.executorStepNamesThatHaveExecuted.entrySet())
        {
            if(keyParts.length == 2 && stepThatHasExecuted.getKey().equals(keyParts[0].substring(2)))
            {
                entry = StringUtils.injectValue(entry, placeholder, stepThatHasExecuted.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2)));
            }
        }
        return entry;
    }


    public static void processReplacementsInStepUsingStepsAlreadyExecuted(String[] keyParts, Map.Entry<String, String> entry, String placeholder)
    {
        for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.stepNamesThatHaveExecuted.entrySet())
        {
            if(keyParts.length == 2 && stepThatHasExecuted.getKey().equals(keyParts[0].substring(2)))
            {
                StringUtils.injectValue(entry, placeholder, stepThatHasExecuted.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2)));
            }
        }
        for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.executorStepNamesThatHaveExecuted.entrySet())
        {
            if(keyParts.length == 2 && stepThatHasExecuted.getKey().equals(keyParts[0].substring(2)))
            {
                StringUtils.injectValue(entry, placeholder, stepThatHasExecuted.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2)));
            }
        }
    }


    public static void processReplacementsInExecutorStepUsingExecutorInput(Map.Entry<String, String> entry, Map<String, String> executorInput)
    {
        List<String> placeholders = new ArrayList<>();
        Pattern p = Pattern.compile("\\{\\{[^}]+\\}\\}");
        Matcher m = p.matcher(entry.getKey());
        while(m.find())
        {
            placeholders.add(m.group());
        }
        for(String placeholder : placeholders)
        {
            if(placeholder.startsWith("{{input."))
            {
                String[] keyParts = placeholder.split("\\.");
                for(Entry<String, String> input : executorInput.entrySet())
                {
                    if(keyParts.length == 2 && input.getKey().equals(keyParts[0].substring(2)))
                    {
                        injectValue(entry, placeholder, keyParts[1].substring(0, keyParts[1].length() - 2));
                    }
                }
            }
        }
    }
}
