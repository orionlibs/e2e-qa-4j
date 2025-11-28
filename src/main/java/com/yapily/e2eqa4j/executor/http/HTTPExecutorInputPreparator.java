package com.yapily.e2eqa4j.executor.http;

import com.yapily.e2eqa4j.TestLIVEData;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HTTPExecutorInputPreparator
{
    public HttpHeaders headers;
    @Autowired HTTPHeaderService httpHeaderService;


    public void prepare(Executor.Step step, Executor executor)
    {
        for(Map.Entry<String, String> entry : step.headers.entrySet())
        {
            TestLIVEData.globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            List<String> placeholders = new ArrayList<>();
            Pattern p = Pattern.compile("\\{\\{[^}]+\\}\\}");
            Matcher m = p.matcher(entry.getValue());
            while(m.find())
            {
                placeholders.add(m.group());
            }
            for(String placeholder : placeholders)
            {
                if(placeholder.startsWith("{{input."))
                {
                    String[] keyParts = placeholder.split("\\.");
                    for(Entry<String, String> executorInput : executor.input.entrySet())
                    {
                        if(keyParts.length == 2 && executorInput.getKey().equals(keyParts[0].substring(2)))
                        {
                            StringUtils.injectValue(entry, placeholder, keyParts[1].substring(0, keyParts[1].length() - 2));
                        }
                    }
                }
                else
                {
                    String[] keyParts = placeholder.split("\\.");
                    for(Map.Entry<String, Map<String, String>> executorStepResultPlusVars : TestLIVEData.executorStepNamesThatHaveExecuted.entrySet())
                    {
                        if(keyParts.length == 2 && executorStepResultPlusVars.getKey().equals(keyParts[0].substring(2)))
                        {
                            String replacement = executorStepResultPlusVars.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2));
                            StringUtils.injectValue(entry, placeholder, replacement);
                        }
                    }
                }
            }
        }
        System.out.println("Step headers: " + step.headers);
        String updatedBody = step.body;
        for(Map.Entry<String, String> globalVariable : TestLIVEData.globalVariables.entrySet())
        {
            updatedBody = StringUtils.injectValue(updatedBody, globalVariable.getKey(), globalVariable.getValue());
        }
        List<String> placeholders = new ArrayList<>();
        Pattern p = Pattern.compile("\\{\\{[^}]+\\}\\}");
        Matcher m = p.matcher(updatedBody);
        while(m.find())
        {
            placeholders.add(m.group());
        }
        for(String placeholder : placeholders)
        {
            if(placeholder.startsWith("{{input."))
            {
                String[] keyParts = placeholder.split("\\.");
                for(Entry<String, String> executorInput : executor.input.entrySet())
                {
                    if(keyParts.length == 2 && executorInput.getKey().equals(keyParts[1].substring(0, keyParts[1].length() - 2)))
                    {
                        updatedBody = StringUtils.injectValue(updatedBody, placeholder, executorInput.getValue());
                    }
                }
            }
            else
            {
                String[] keyParts = placeholder.split("\\.");
                for(Map.Entry<String, Map<String, String>> executorStepResultPlusVars : TestLIVEData.executorStepNamesThatHaveExecuted.entrySet())
                {
                    if(keyParts.length == 2 && executorStepResultPlusVars.getKey().equals(keyParts[0].substring(2)))
                    {
                        String replacement = executorStepResultPlusVars.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2));
                        updatedBody = StringUtils.injectValue(updatedBody, placeholder, replacement);
                    }
                }
            }
        }
        step.body = updatedBody;
        System.out.println("Step request body: " + step.body);
        String updatedURL = step.url;
        for(Map.Entry<String, String> globalVariable : TestLIVEData.globalVariables.entrySet())
        {
            updatedURL = StringUtils.injectValue(updatedURL, globalVariable.getKey(), globalVariable.getValue());
        }
        List<String> placeholders1 = new ArrayList<>();
        Pattern p1 = Pattern.compile("\\{\\{[^}]+\\}\\}");
        Matcher m1 = p1.matcher(updatedURL);
        while(m1.find())
        {
            placeholders1.add(m1.group());
        }
        for(String placeholder : placeholders1)
        {
            if(placeholder.startsWith("{{input."))
            {
                String[] keyParts = placeholder.split("\\.");
                for(Entry<String, String> executorInput : executor.input.entrySet())
                {
                    if(keyParts.length == 2 && executorInput.getKey().equals(keyParts[1].substring(0, keyParts[1].length() - 2)))
                    {
                        updatedURL = StringUtils.injectValue(updatedURL, placeholder, executorInput.getValue());
                    }
                }
            }
            else
            {
                String[] keyParts = placeholder.split("\\.");
                for(Map.Entry<String, Map<String, String>> executorStepResultPlusVars : TestLIVEData.executorStepNamesThatHaveExecuted.entrySet())
                {
                    if(keyParts.length == 2 && executorStepResultPlusVars.getKey().equals(keyParts[0].substring(2)))
                    {
                        String replacement = executorStepResultPlusVars.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2));
                        updatedURL = StringUtils.injectValue(updatedURL, placeholder, replacement);
                    }
                }
            }
        }
        step.url = updatedURL;
        headers = httpHeaderService.convertToHttpHeaders(step.headers);
    }
}
