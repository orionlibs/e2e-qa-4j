package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.executor.ExecutorType;
import com.yapily.e2eqa4j.executor.http.APICallResult;
import com.yapily.e2eqa4j.executor.http.HTTPExecutor;
import com.yapily.e2eqa4j.executor.http.HTTPHeaderService;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.test_suite_runner.test_case.TestLIVEData;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
class ExecutorStepExecutorRunner
{
    @Autowired HTTPExecutor httpExecutor;
    @Autowired HTTPHeaderService httpHeaderService;


    void run(ExecutorRunner executorRunner, List<Executor> executors, Executor executor, Executor.Step step, TestSuite.Step testCaseStep, TestSuite.Testcase testCase, Map<String, String> globalVariables, Executor.StepResult lastStepResult)
    {
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
                for(Map.Entry<String, String> stepVar : step.vars.entrySet())
                {
                    StringUtils.injectValue(entry, stepVar.getKey(), stepVar.getValue());
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
            String updatedURL = step.url;
            for(Map.Entry<String, String> globalVariable : globalVariables.entrySet())
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
            }
            step.url = updatedURL;
            if(executorToRun.executor.equals(step.type))
            {
                Map<String, String> nestedExecutorOutput = executorRunner.runExecutor(globalVariables, executorToRun, executors, testCase, testCaseStep);
                step.result.result.putAll(nestedExecutorOutput);
                if(testCase != null)
                {
                    testCase.result.putAll(nestedExecutorOutput);
                }
                break;
            }
            else if(ExecutorType.HTTP.name().equalsIgnoreCase(step.type))
            {
                HttpHeaders headers = httpHeaderService.convertToHttpHeaders(step.headers);
                APICallResult apiCallResult = httpExecutor.run(step.method, step.url, headers, step.body);
                step.result.body = apiCallResult.jsonResponseBody;
                step.result.statusCode = apiCallResult.statusCode;
                step.result.headers = apiCallResult.headers;
                if(testCase != null)
                {
                    testCase.result.put("body", step.result.body);
                    testCase.result.put("statusCode", Integer.toString(step.result.statusCode));
                }
                if(testCaseStep != null)
                {
                    testCaseStep.result.headers.putAll(step.result.headers);
                }
                break;
            }
            for(Map.Entry<String, String> entry : step.vars.entrySet())
            {
                globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
                step.result.result.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, "result." + entry1.getKey(), entry1.getValue()));
                if(lastStepResult != null)
                {
                    if(lastStepResult.result != null)
                    {
                        lastStepResult.result.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
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
            TestLIVEData.stepNamesThatHaveExecuted.put(step.type, new HashMap<>(step.result.result));
        }
    }
}
