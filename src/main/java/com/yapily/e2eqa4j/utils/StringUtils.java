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


    public static void injectValue(Map.Entry<String, Object> entry, String placeholderToReplace, String replacement)
    {
        String regex = "(?s)\\{\\{" + placeholderToReplace + "\\}\\}";
        entry.setValue(entry.getValue()
                        .toString()
                        .replaceAll(regex, replacement));
    }
}
