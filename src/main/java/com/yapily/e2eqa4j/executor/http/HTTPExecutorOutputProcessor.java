package com.yapily.e2eqa4j.executor.http;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import org.springframework.stereotype.Component;

@Component
public class HTTPExecutorOutputProcessor
{
    public void process(Executor.Step step, APICallResult apiCallResult, TestSuite.Step testCaseStep, TestSuite.Testcase testCase)
    {
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
    }
}
