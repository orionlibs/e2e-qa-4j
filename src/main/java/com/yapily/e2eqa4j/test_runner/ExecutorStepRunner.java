package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.executor.ExecutorType;
import com.yapily.e2eqa4j.executor.http.APICallResult;
import com.yapily.e2eqa4j.executor.http.HTTPExecutor;
import com.yapily.e2eqa4j.executor.http.HTTPHeaderService;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
class ExecutorStepRunner
{
    @Autowired HTTPExecutor httpExecutor;
    @Autowired HTTPHeaderService httpHeaderService;


    Executor.StepResult runExecutorStep(ExecutorRunner executorRunner, Map<String, String> globalVariables, List<Executor> executors, Executor.Step step, Executor.StepResult lastStepResult)
    {
        //set actual values in all step.input
        //set actual values in all step.headers
        //set actual values in all step.body
        System.out.println("Executor step type: " + step.type);
        step.vars.forEach((k, v) -> System.out.println("Step result var: " + k + " -> " + v));
        step.assertions.forEach(k -> System.out.println("Assertion: " + k));
        step.input.putAll(globalVariables);
        for(Map.Entry<String, Object> entry : step.input.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectObjectValue(entry, entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null && lastStepResult.result != null)
            {
                lastStepResult.result.entrySet().forEach(entry1 -> StringUtils.injectObjectValue(entry, entry1.getKey(), (String)entry1.getValue()));
            }
        }
        System.out.println("Step input vars: " + step.input);
        System.out.println("Running step: " + step.type);
        System.out.println("Step headers: " + step.headers);
        System.out.println("Step request body: " + step.body);
        for(Executor executor : executors)
        {
            if(executor.executor.equals(step.type))
            {
                executorRunner.runExecutor(globalVariables, executor, executors);
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
