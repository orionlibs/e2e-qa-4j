package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.Logger;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Step;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TestStepRunner
{
    @Autowired ExecutorRunner executorRunner;


    TestSuite.StepResult runStep(Map<String, String> globalVariables, List<Executor> executors, TestSuite.Testcase testCase, Step step, TestSuite.StepResult lastStepResult)
    {
        //System.out.println("Step type: " + step.type);
        for(Map.Entry<String, String> entry : step.input.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null)
            {
                if(lastStepResult.output != null)
                {
                    lastStepResult.output.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, "result." + entry1.getKey(), entry1.getValue()));
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
        //step.input.forEach((k, v) -> System.out.println(">>>>>>>>>>>>Updated step input var: " + k + " -> " + v));
        //step.vars.forEach((k, v) -> System.out.println("Step result var: " + k + " -> " + v));
        //step.assertions.forEach(k -> System.out.println("Assertion: " + k));
        System.out.println("Step input vars: " + step.input);
        System.out.println("Running step: " + step.type);
        Map<String, String> executorOutput = new HashMap<>();
        for(Executor executor : executors)
        {
            if(executor.executor.equals(step.type))
            {
                executor.input.putAll(step.input);
                for(Map.Entry<String, String> entry : executor.input.entrySet())
                {
                    for(Map.Entry<String, String> testResultVar : testCase.result.entrySet())
                    {
                        StringUtils.injectValue(entry, testResultVar.getKey(), testResultVar.getValue());
                    }
                }
                //System.out.println("Executor input vars: " + executor.input);
                executorOutput.putAll(executorRunner.runExecutor(globalVariables, executor, executors, testCase, step));
                break;
            }
        }
        step.result.output.putAll(executorOutput);
        TestLIVEData.stepNamesThatHaveExecuted.put(testCase.name + "." + step.type, new HashMap<>(step.result.output));
        //System.out.println("steps that have executed: " + TestLIVEData.stepNamesThatHaveExecuted.entrySet());
        for(Map.Entry<String, String> entry : step.vars.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            step.result.output.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, "result." + entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null)
            {
                if(lastStepResult.output != null)
                {
                    lastStepResult.output.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
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
            testCase.result.put(entry.getKey(), entry.getValue());
        }
        testCase.result.putAll(step.result.output);
        step.result.output.forEach((k, v) -> System.out.println("step output var: " + k + " -> " + v));
        step.vars.forEach((k, v) -> System.out.println("Updated step result var: " + k + " -> " + v));
        for(String log : step.log)
        {
            String updatedLog = log;
            for(Map.Entry<String, String> globalVariable : globalVariables.entrySet())
            {
                updatedLog = StringUtils.injectValue(updatedLog, globalVariable.getKey(), globalVariable.getValue());
            }
            for(Map.Entry<String, String> stepVar : step.vars.entrySet())
            {
                updatedLog = StringUtils.injectValue(updatedLog, stepVar.getKey(), stepVar.getValue());
            }
            for(Map.Entry<String, String> testResultVar : testCase.result.entrySet())
            {
                updatedLog = StringUtils.injectValue(updatedLog, testResultVar.getKey(), testResultVar.getValue());
            }
            List<String> placeholders = new ArrayList<>();
            Pattern p = Pattern.compile("\\{\\{[^}]+\\}\\}");
            Matcher m = p.matcher(updatedLog);
            while(m.find())
            {
                placeholders.add(m.group());
            }
            for(String placeholder : placeholders)
            {
                if(placeholder.startsWith("{{result."))
                {
                    if(placeholder.equals("{{result.statusCode}}"))
                    {
                        updatedLog = StringUtils.injectValue(updatedLog, "result.statusCode", testCase.result.get("statusCode"));
                    }
                    else if(placeholder.equals("{{result.body}}"))
                    {
                        updatedLog = StringUtils.injectValue(updatedLog, "result.body", testCase.result.get("body"));
                    }
                    else if(placeholder.equals("{{result.headers}}"))
                    {
                        updatedLog = StringUtils.injectValue(updatedLog, "result.headers", step.result.headers.toString());
                    }
                    if(placeholder.startsWith("{{result.headers."))
                    {
                        String[] headerVarParts = placeholder.split("\\.");
                        String headerValue = step.result.headers.get(headerVarParts[headerVarParts.length - 1]);
                        updatedLog = StringUtils.injectValue(updatedLog, placeholder.substring(2, placeholder.length() - 2), headerValue);
                    }
                    for(Map.Entry<String, String> testCaseResultVar : testCase.result.entrySet())
                    {
                        if(testCaseResultVar.getKey().equals(placeholder.substring(2, placeholder.length() - 2).replace("result.", "")))
                        {
                            updatedLog = StringUtils.injectValue(updatedLog, "result." + testCaseResultVar.getKey(), testCaseResultVar.getValue());
                        }
                    }
                }
                else if(placeholder.indexOf(".") != -1)
                {
                    String[] keyParts = placeholder.split("\\.");
                    for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.stepNamesThatHaveExecuted.entrySet())
                    {
                        if(keyParts.length == 2 && stepThatHasExecuted.getKey().equals(keyParts[0].substring(2)))
                        {
                            updatedLog = StringUtils.injectValue(updatedLog, placeholder, stepThatHasExecuted.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2)));
                        }
                    }
                }
            }
            Logger.info(updatedLog);
        }
        return step.result;
    }
}
