package com.yapily.e2eqa4j.utils;

import java.util.Map;
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


    public static void injectStringValue(Map.Entry<String, String> entry, String placeholderToReplace, String replacement)
    {
        String regex = Pattern.quote(placeholderToReplace);
        if(replacement == null)
        {
            replacement = placeholderToReplace;
        }
        entry.setValue(entry.getValue()
                        .toString()
                        .replace("\\{", "")
                        .replace("\\}", "")
                        .replaceAll(regex, replacement.replace("\\{", "")
                                        .replace("\\}", "")));
    }
}
