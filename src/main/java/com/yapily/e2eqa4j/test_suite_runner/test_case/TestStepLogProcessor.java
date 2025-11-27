package com.yapily.e2eqa4j.test_suite_runner.test_case;

import com.yapily.e2eqa4j.Logger;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Step;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class TestStepLogProcessor
{
    public void process(Step step, TestSuite.Testcase testCase, Map<String, String> globalVariables)
    {
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
                    updatedLog = StringUtils.processReplacementsInTestStepUsingStepsAlreadyExecuted(keyParts, updatedLog, placeholder);
                }
            }
            Logger.info(updatedLog);
        }
    }
}
