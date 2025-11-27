package com.yapily.e2eqa4j.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSuite
{
    public String name;
    public List<Testcase> testcases;
    public Setup setup;


    public static class Setup
    {
        public List<Step> steps;
    }


    public static class Testcase
    {
        public String name;
        public List<Step> steps;
        public Map<String, String> result = new HashMap<>();
    }


    public static class Step
    {
        public String type;
        public Map<String, String> vars = new HashMap<>();
        public List<String> assertions = new ArrayList<>();
        public Map<String, String> input = new HashMap<>();
        public List<String> log = new ArrayList<>();
        public StepResult result = new StepResult();
    }


    public static class StepResult
    {
        public Map<String, String> output = new HashMap<>();
    }
}
