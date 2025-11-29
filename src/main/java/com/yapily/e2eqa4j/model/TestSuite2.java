package com.yapily.e2eqa4j.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestSuite2
{
    private static final String NAME_KEY = "name";
    private static final String TESTS_KEY = "testcases";
    private static final String ARRAY_KEY = "array";
    private static final String VARS_KEY = "vars";
    private static final String SETUP_KEY = "setup";
    public YAMLNode yaml;
    public String name;
    public List<Testcase> testcases = new ArrayList<>();
    public Setup setup;
    public Map<String, String> vars = new HashMap<>();
    public List<Map<String, String>> array = new ArrayList<>();


    public TestSuite2()
    {
    }


    public TestSuite2(YAMLNode yaml)
    {
        this.yaml = yaml;
    }


    public String getName()
    {
        String nameTemp = "no test suite name";
        nameTemp = yaml.getChild(NAME_KEY)
                        .flatMap(YAMLNode::getValue)
                        .map(String::valueOf)
                        .orElse(nameTemp);
        name = nameTemp;
        return nameTemp;
    }


    public Map<String, String> getVars()
    {
        Map<String, String> result = new HashMap<>();
        yaml.getChild(VARS_KEY).ifPresent(varsNode -> {
            flattenToMap(varsNode, "", result);
        });
        vars.forEach((k, v) -> result.put(k, v));
        return result;
    }


    public Map<String, String> getArray()
    {
        Map<String, String> arrayData = new HashMap<>();
        yaml.getChild(ARRAY_KEY).ifPresent(arrayNode -> {
            flattenToMap(arrayNode, ARRAY_KEY, arrayData);
        });
        arrayData.forEach((k, v) -> array.add(Map.of(k, v)));
        return arrayData;
    }


    public Setup getSetup()
    {
        Map<String, String> result = new LinkedHashMap<>();
        yaml.getChild(SETUP_KEY).ifPresent(setupNode -> {
            flattenToMap(setupNode, "setup", result);
        });
        Setup setupTemp = new Setup();
        for(Map.Entry<String, String> entry : result.entrySet())
        {
            Step stepTemp = new Step();
            stepTemp.type = entry.getValue();
            setupTemp.steps.add(stepTemp);
        }
        return setupTemp;
    }


    public List<Testcase> getTestCases()
    {
        List<Testcase> testcases = new ArrayList<>();
        //Map<String, String> data = new HashMap<>();
        yaml.getChild(TESTS_KEY).ifPresent(node -> {
            Optional<Object> z = node.getValue();
            if(z.isPresent())
            {
                List<YAMLNode> tests = (List)z.get();
                for(YAMLNode test : tests)
                {
                    String testCaseName = (String)((YAMLNode)test.getChildren().get("name").getValue().get()).getValue().get();
                    List<YAMLNode> testCaseSteps = (List<YAMLNode>)((YAMLNode)test.getChildren().get("steps").getValue().get()).getValue().get();
                    for(YAMLNode testStep : testCaseSteps)
                    {

                    }
                }
            }
        });
        //data.forEach((k, v) -> array.add(Map.of(k, v)));
        return testcases;
    }


    private void flattenToMap(YAMLNode node, String prefix, Map<String, String> result)
    {
        // First check if this node itself has a value (could be a list)
        node.getValue().ifPresent(value -> {
            if(value instanceof List<?> list)
            {
                // Handle list
                for(int i = 0; i < list.size(); i++)
                {
                    Object element = list.get(i);
                    String indexedKey = prefix.isEmpty() ? String.valueOf(i) : prefix + "." + i;
                    if(element instanceof YAMLNode yamlNode)
                    {
                        // List element is a map/object - recurse into it
                        flattenToMap(yamlNode, indexedKey, result);
                    }
                    else
                    {
                        // List element is a scalar
                        result.put(indexedKey, String.valueOf(element));
                    }
                }
                return; // Don't process children after handling list
            }
        });
        // Process child nodes (for maps)
        for(Map.Entry<String, YAMLNode> entry : node.getChildren().entrySet())
        {
            String key = entry.getKey();
            YAMLNode childNode = entry.getValue();
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            if(childNode.isLeaf())
            {
                // It's a leaf node, get its value
                childNode.getValue().ifPresent(value -> {
                    if(!(value instanceof List<?>))
                    {
                        result.put(fullKey, String.valueOf(value));
                    }
                });
            }
            else
            {
                // It's a nested node, recurse
                flattenToMap(childNode, fullKey, result);
            }
        }
    }


    public static class Setup
    {
        public List<Step> steps = new ArrayList<>();
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


    public static class Testcase
    {
        public String name;
        public List<Step> steps = new ArrayList<>();
        public Map<String, String> result = new HashMap<>();
    }


    public static class StepResult
    {
        public Map<String, String> output = new HashMap<>();
        public Map<String, String> headers = new HashMap<>();
    }
}
