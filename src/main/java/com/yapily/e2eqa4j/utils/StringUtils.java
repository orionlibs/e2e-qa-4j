package com.yapily.e2eqa4j.utils;

import java.util.Map;

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
}
