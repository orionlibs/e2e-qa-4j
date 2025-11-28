package com.yapily.e2eqa4j;

import com.yapily.e2eqa4j.command.run_tests.RunTestsCommand;
import com.yapily.e2eqa4j.input_processor.RunTestsCommandInputProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RunnerEntryPoint implements ApplicationRunner
{
    @Autowired RunTestsCommandInputProcessor runTestsCommandInputProcessor;
    @Autowired RunTestsCommand runTestsCommand;


    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        String[] sourceArgs = args.getSourceArgs();
        if(sourceArgs.length >= 1)
        {
            List<String> tokens = new ArrayList<>(Arrays.asList(sourceArgs));
            // If the first token is the command name (e.g. "run"), drop it
            if(!tokens.isEmpty())
            {
                if("run".equals(tokens.get(0)))
                {
                    tokens.remove(0);
                    runTestsCommandInputProcessor.process(tokens);
                    runTestsCommand.runTests(runTestsCommandInputProcessor.path, runTestsCommandInputProcessor.libDir, runTestsCommandInputProcessor.globalVars);
                }
            }
        }
        System.exit(0);
    }
}
