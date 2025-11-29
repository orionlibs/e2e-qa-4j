package com.yapily.e2eqa4j.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestSuite2
{
    private static final String TESTS_KEY = "testcases";
    private static final String ARRAY_KEY = "array";
    private static final String VARS_KEY = "vars";
    public YAMLNode yaml;
    public String name;
    public List<Testcase> testcases;
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


    /**
     * Returns the list of test-case nodes under the "tests" key.
     * Each element is returned as a YamlNode (if element was scalar it will be wrapped).
     */
    public List<YAMLNode> getTestCases()
    {
        return yaml.getChild(TESTS_KEY)
                        .flatMap(n -> n.getValue().map(v -> v))
                        .map(v -> {
                            if(v instanceof List<?>)
                            {
                                List<?> raw = (List<?>)v;
                                List<YAMLNode> out = new ArrayList<>(raw.size());
                                for(Object e : raw)
                                {
                                    if(e instanceof YAMLNode)
                                    {
                                        out.add((YAMLNode)e);
                                    }
                                    else
                                    {
                                        // wrap scalars or other objects into a YamlNode holding the value
                                        out.add(new YAMLNode(e));
                                    }
                                }
                                return out;
                            }
                            return Collections.<YAMLNode>emptyList();
                        })
                        .orElse(Collections.emptyList());
    }


    /**
     * Find a test case by its 'name' scalar property.
     * Returns the first match or empty if not found.
     */
    public Optional<YAMLNode> findTestByName(String name)
    {
        return getTestCases().stream()
                        .filter(tc -> tc.getChild("name")
                                        .flatMap(YAMLNode::getValue)
                                        .map(Object::toString)
                                        .map(name::equals)
                                        .orElse(false))
                        .findFirst();
    }


    /**
     * Add a test case node to the tests list (creates the list if necessary).
     */
    public void addTestCase(YAMLNode testCase)
    {
        // access the 'tests' child (create if missing)
        Map<String, YAMLNode> children = yaml.getChildren();
        YAMLNode testsNode = children.get(TESTS_KEY);
        if(testsNode == null)
        {
            // create a new list wrapper node and put it as 'tests'
            testsNode = new YAMLNode(new ArrayList<>());
            children.put(TESTS_KEY, testsNode);
        }
        // testsNode.value is expected to be a List<Object>
        Object maybeList = testsNode.getValue().orElse(null);
        if(maybeList instanceof List<?>)
        {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>)maybeList;
            list.add(testCase);
        }
        else
        {
            // not a list for some reason: replace with a new list containing existing value and the new node
            List<Object> newList = new ArrayList<>();
            if(maybeList != null)
            {
                newList.add(maybeList);
            }
            newList.add(testCase);
            // replace the testsNode's internal value
            // we must access testsNode's internal state; use reflection-less approach:
            children.put(TESTS_KEY, new YAMLNode(newList));
        }
    }


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
        public Map<String, String> headers = new HashMap<>();
    }
}
