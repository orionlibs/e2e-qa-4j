package com.yapily.e2eqa4j;

import com.yapily.e2eqa4j.executor.ExecutorType;
import com.yapily.e2eqa4j.executor.http.APICallResult;
import com.yapily.e2eqa4j.executor.http.HTTPExecutor;
import com.yapily.e2eqa4j.executor.http.HTTPHeaderService;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Step;
import com.yapily.e2eqa4j.model.TestSuite.Testcase;
import com.yapily.e2eqa4j.utils.StringUtils;
import com.yapily.e2eqa4j.utils.YAMLUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class TestRunner
{
    @Autowired YAMLUtils yamlUtils;
    @Autowired HTTPExecutor httpExecutor;
    @Autowired HTTPHeaderService httpHeaderService;
    Map<String, String> allVars = new HashMap<>();


    public void runTest(File testFile, List<File> libraryFiles, Map<String, String> globalVariables) throws IOException
    {
        List<Executor> executors = yamlUtils.loadLibraries(libraryFiles);
        try(FileInputStream fis = new FileInputStream(testFile);
                        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8))
        {
            TestSuite testSuite = yamlUtils.loadTestSuite(isr);
            System.out.println("Running test suite: " + testSuite.name);
            testSuite.setup.steps.forEach(s -> System.out.println("setup steps: " + s.type));
            for(Executor executor : executors)
            {
                for(TestSuite.Step setupExecutor : testSuite.setup.steps)
                {
                    if(executor.executor.equals(setupExecutor.type))
                    {
                        Map<String, String> executorOutput = runExecutor(globalVariables, executor, executors);
                        globalVariables.putAll(executorOutput);
                        break;
                    }
                }
            }
            System.out.println("Running test suite: " + testSuite.setup);
            runTestCases(globalVariables, testSuite, executors);
        }
    }


    private void runTestCases(Map<String, String> globalVariables, TestSuite testSuite, List<Executor> executors)
    {
        for(TestSuite.Testcase testcase : testSuite.testcases)
        {
            runTestCase(globalVariables, executors, testcase);
        }
    }


    private void runTestCase(Map<String, String> globalVariables, List<Executor> executors, Testcase testcase)
    {
        System.out.println("Testcase: " + testcase.name);
        TestSuite.StepResult lastStepResult = null;
        for(TestSuite.Step step : testcase.steps)
        {
            lastStepResult = runStep(globalVariables, executors, step, lastStepResult);
        }
    }


    private TestSuite.StepResult runStep(Map<String, String> globalVariables, List<Executor> executors, Step step, TestSuite.StepResult lastStepResult)
    {
        System.out.println("Step type: " + step.type);
        step.vars.forEach((k, v) -> System.out.println("Step result var: " + k + " -> " + v));
        step.assertions.forEach(k -> System.out.println("Assertion: " + k));
        step.input.putAll(globalVariables);
        System.out.println("Step input vars: " + step.input);
        System.out.println("Running step: " + step.type);
        //set actual values in all step.input
        for(Map.Entry<String, Object> entry : step.input.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null && lastStepResult.output != null)
            {
                lastStepResult.output.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            }
        }
        step.input.forEach((k, v) -> System.out.println("Updated step result var: " + k + " -> " + v));
        Map<String, String> executorOutput = new HashMap<>();
        for(Executor executor : executors)
        {
            if(executor.executor.equals(step.type))
            {
                executorOutput.putAll(runExecutor(globalVariables, executor, executors));
                break;
            }
        }
        //set actual values in all step.vars
        //process assertions
        //set actual values in all step.result plus use executorOutput
        //process info
        return step.result;
    }


    private Map<String, String> runExecutor(Map<String, String> globalVariables, Executor executor, List<Executor> executors)
    {
        System.out.println("Running executor: " + executor.executor);
        Executor.StepResult lastStepResult = null;
        for(Executor.Step step : executor.steps)
        {
            lastStepResult = runExecutorStep(globalVariables, executors, step, lastStepResult);
        }
        //set actual values in all executor.output
        executor.output.forEach((k, v) -> System.out.println("Executor output var: " + k + " -> " + v));
        return executor.output;
    }


    private Executor.StepResult runExecutorStep(Map<String, String> globalVariables, List<Executor> executors, Executor.Step step, Executor.StepResult lastStepResult)
    {
        //set actual values in all step.input
        //set actual values in all step.headers
        //set actual values in all step.body
        System.out.println("Executor step type: " + step.type);
        step.vars.forEach((k, v) -> System.out.println("Step result var: " + k + " -> " + v));
        step.assertions.forEach(k -> System.out.println("Assertion: " + k));
        step.input.putAll(globalVariables);
        System.out.println("Step input vars: " + step.input);
        System.out.println("Running step: " + step.type);
        System.out.println("Step headers: " + step.headers);
        System.out.println("Step request body: " + step.body);
        for(Executor executor : executors)
        {
            if(executor.executor.equals(step.type))
            {
                runExecutor(globalVariables, executor, executors);
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


    public void runTest(String testFilePath, List<File> libraryFiles, Map<String, String> globalVariables) throws IOException
    {
        runTest(new File(testFilePath), libraryFiles, globalVariables);
    }
}
