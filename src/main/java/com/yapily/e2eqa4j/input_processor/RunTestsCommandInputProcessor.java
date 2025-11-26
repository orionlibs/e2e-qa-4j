package com.yapily.e2eqa4j.input_processor;

import com.yapily.e2eqa4j.utils.StringUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RunTestsCommandInputProcessor
{
    public String path;
    public String libDir;
    public Map<String, String> vars = new HashMap<>();


    public void process(List<String> tokens) throws IOException
    {
        for(int i = 0; i < tokens.size(); i++)
        {
            String t = tokens.get(i);
            if(!t.startsWith("--"))
            {
                continue;
            }
            String key;
            String value = null;
            int eq = t.indexOf('=');
            if(eq > 0)
            {
                key = t.substring(0, eq);
                value = t.substring(eq + 1);
                value = StringUtils.stripQuotes(value);
            }
            else
            {
                key = t;
                // value may be the next token if it doesn't start with --
                if(i + 1 < tokens.size() && !tokens.get(i + 1).startsWith("--"))
                {
                    value = tokens.get(i + 1);
                    value = StringUtils.stripQuotes(value);
                    i++;
                }
                else
                {
                    // flag without value (treat as boolean true if needed)
                    value = null;
                }
            }
            switch(key)
            {
                case "--path":
                    path = value;
                    break;
                case "--lib-dir":
                    libDir = value;
                    break;
                case "--var":
                    if(value != null)
                    {
                        String[] varNameAndValue = value.split("=");
                        vars.put(varNameAndValue[0], varNameAndValue[1]);
                    }
                    break;
                default:
                    // unknown option - ignore or log
                    System.out.println("Unknown option: " + key + (value != null ? " => " + value : ""));
            }
        }
        System.out.println("Parsed path   = " + path);
        System.out.println("Parsed libDir = " + libDir);
        System.out.println("Parsed vars   = " + vars);
    }
}
