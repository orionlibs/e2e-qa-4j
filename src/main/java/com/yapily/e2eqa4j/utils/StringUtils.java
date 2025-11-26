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


    public static void injectObjectValue(Map.Entry<String, Object> entry, String placeholderToReplace, String replacement)
    {
        String regex = "(?s)\\{\\{" + placeholderToReplace + "\\}\\}";
        if(!placeholderToReplace.startsWith("\\{\\{") && !placeholderToReplace.endsWith("\\{\\{"))
        {
            regex = "(?s)" + placeholderToReplace;
        }
        else if(placeholderToReplace.startsWith("\\{\\{") && !placeholderToReplace.endsWith("\\{\\{"))
        {
            regex = "(?s)\\{\\{" + placeholderToReplace.substring(2);
        }
        else if(!placeholderToReplace.startsWith("\\{\\{") && placeholderToReplace.endsWith("\\{\\{"))
        {
            regex = "(?s)" + placeholderToReplace.substring(0, placeholderToReplace.length() - 3) + "\\}\\}";
        }
        if(replacement != null)
        {
            if(replacement.startsWith("\\{\\{") && replacement.endsWith("\\{\\{"))
            {
                replacement = replacement.substring(2, replacement.length() - 3);
            }
            else if(replacement.startsWith("\\{\\{") && !replacement.endsWith("\\{\\{"))
            {
                replacement = replacement.substring(2);
            }
            else if(!replacement.startsWith("\\{\\{") && replacement.endsWith("\\{\\{"))
            {
                replacement = "(?s)" + replacement.substring(0, replacement.length() - 3);
            }
        }
        else
        {
            replacement = placeholderToReplace;
        }
        entry.setValue(entry.getValue()
                        .toString()
                        .replaceAll(regex, replacement));
    }


    public static void injectStringValue(Map.Entry<String, String> entry, String placeholderToReplace, String replacement)
    {
        String regex = "(?s)\\{\\{" + placeholderToReplace + "\\}\\}";
        if(!placeholderToReplace.startsWith("\\{\\{") && !placeholderToReplace.endsWith("\\{\\{"))
        {
            regex = "(?s)" + placeholderToReplace;
        }
        else if(placeholderToReplace.startsWith("\\{\\{") && !placeholderToReplace.endsWith("\\{\\{"))
        {
            regex = "(?s)\\{\\{" + placeholderToReplace.substring(2);
        }
        else if(!placeholderToReplace.startsWith("\\{\\{") && placeholderToReplace.endsWith("\\{\\{"))
        {
            regex = "(?s)" + placeholderToReplace.substring(0, placeholderToReplace.length() - 3) + "\\}\\}";
        }
        if(replacement != null)
        {
            if(replacement.startsWith("\\{\\{") && replacement.endsWith("\\{\\{"))
            {
                replacement = replacement.substring(2, replacement.length() - 3);
            }
            else if(replacement.startsWith("\\{\\{") && !replacement.endsWith("\\{\\{"))
            {
                replacement = replacement.substring(2);
            }
            else if(!replacement.startsWith("\\{\\{") && replacement.endsWith("\\{\\{"))
            {
                replacement = "(?s)" + replacement.substring(0, replacement.length() - 3);
            }
        }
        else
        {
            replacement = placeholderToReplace;
        }
        entry.setValue(entry.getValue()
                        .toString()
                        .replaceAll(regex, replacement));
    }
}
