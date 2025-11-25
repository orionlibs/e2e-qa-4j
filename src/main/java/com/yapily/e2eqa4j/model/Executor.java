package com.yapily.e2eqa4j.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Executor
{
    public String executor;
    public Map<String, Object> input = new HashMap<>();
    public List<Step> steps = new ArrayList<>();
    public Map<String, String> output = new HashMap<>();


    public static class Step
    {
        public String type;
        public Map<String, String> vars = new HashMap<>();
        public List<String> assertions = new ArrayList<>();
        public Map<String, Object> input = new HashMap<>();
        public String method;
        public String url;
        public Map<String, String> headers = new HashMap<>();
        public String body;
        public List<String> info = new ArrayList<>();
        public StepResult result = new StepResult();
    }


    public static class StepResult
    {
        public String body;
        public int statusCode;
        public Map<String, String> headers = new HashMap<>();
        public String stdout;
    }
}
