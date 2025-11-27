package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.executor.ExecutorType;
import com.yapily.e2eqa4j.executor.http.APICallResult;
import com.yapily.e2eqa4j.executor.http.HTTPExecutor;
import com.yapily.e2eqa4j.executor.http.HTTPHeaderService;
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
class ExecutorStepRunner
{
    @Autowired HTTPExecutor httpExecutor;
    @Autowired HTTPHeaderService httpHeaderService;


    Executor.StepResult runExecutorStep(ExecutorRunner executorRunner, Executor executor, Map<String, String> globalVariables, List<Executor> executors, Executor.Step step, Executor.StepResult lastStepResult)
    {
        //System.out.println("Executor step type: " + step.type);
        step.vars.forEach((k, v) -> System.out.println("Step result var: " + k + " -> " + v));
        step.assertions.forEach(k -> System.out.println("Assertion: " + k));
        step.input.putAll(globalVariables);
        for(Map.Entry<String, String> entry : step.input.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null && lastStepResult.result != null)
            {
                lastStepResult.result.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), (String)entry1.getValue()));
            }
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
                    for(Entry<String, String> executorInput : executor.input.entrySet())
                    {
                        if(keyParts.length == 2 && executorInput.getKey().equals(keyParts[0].substring(2)))
                        {
                            StringUtils.injectValue(entry, placeholder, keyParts[1].substring(0, keyParts[1].length() - 2));
                        }
                    }
                }
            }
        }
        System.out.println("Step input vars: " + step.input);
        System.out.println("Running step: " + step.type);
        for(Executor executorToRun : executors)
        {
            for(Map.Entry<String, String> entry : step.input.entrySet())
            {
                globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
                if(lastStepResult != null)
                {
                    if(lastStepResult.result != null)
                    {
                        lastStepResult.result.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, "result." + entry1.getKey(), entry1.getValue()));
                    }
                }
                String[] keyParts = entry.getValue().substring(2, entry.getValue().length() - 2).split("\\.");
                for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.stepNamesThatHaveExecuted.entrySet())
                {
                    if(stepThatHasExecuted.getKey().equals(keyParts[0]))
                    {
                        StringUtils.injectValue(entry, entry.getValue(), stepThatHasExecuted.getValue().get(keyParts[1]));
                    }
                }
            }
            executor.input.putAll(step.input);
            executorToRun.input.putAll(step.input);
            for(Map.Entry<String, String> entry : step.headers.entrySet())
            {
                globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
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
                }
            }
            System.out.println("Step headers: " + step.headers);
            String updatedBody = step.body;
            for(Map.Entry<String, String> globalVariable : globalVariables.entrySet())
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
            }
            step.body = updatedBody;
            System.out.println("Step request body: " + step.body);
            if(executorToRun.executor.equals(step.type))
            {
                executorRunner.runExecutor(globalVariables, executorToRun, executors);
                break;
            }
            else if(ExecutorType.HTTP.name().equalsIgnoreCase(step.type))
            {
                HttpHeaders headers = httpHeaderService.convertToHttpHeaders(step.headers);
                APICallResult apiCallResult = httpExecutor.run(step.method, step.url, headers, step.body);
                step.result.body = apiCallResult.jsonResponseBody;
                step.result.statusCode = apiCallResult.statusCode;
                step.result.headers = apiCallResult.headers;
                break;
            }
        }
        //set actual values in all step.vars
        //process assertions
        //set actual values in all step.result
        //process info
        return step.result;
    }
}
