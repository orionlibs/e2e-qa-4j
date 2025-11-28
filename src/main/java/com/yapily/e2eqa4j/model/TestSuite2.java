package com.yapily.e2eqa4j.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestSuite2
{
    private static final String TESTS_KEY = "testcases";
    private static final String VARS_KEY = "vars";
    public YAMLNode yaml;
    public String name;
    public List<Testcase> testcases;
    public Setup setup;
    public Map<String, String> vars = new HashMap<>();


    public TestSuite2()
    {
    }


    public TestSuite2(YAMLNode yaml)
    {
        this.yaml = yaml;
    }


    public Map<String, String> getVars()
    {
        return yaml.getChild(VARS_KEY)
                        .flatMap(n -> n.getValue().map(v -> v))
                        .map(v -> {
                            Map<String, String> result = new LinkedHashMap<>();
                            // Case: vars is a List
                            if(v instanceof List<?> list)
                            {
                                for(Object elem : list)
                                {
                                    // Element already converted to YAMLNode (most common when element is a map)
                                    /*if(elem instanceof YAMLNode node)
                                    {
                                        // common shape: { name: ..., value: ... }
                                        String name = node.getChild("name").flatMap(YAMLNode::getValue).map(Object::toString).orElse(null);
                                        String val = node.getChild("value").flatMap(YAMLNode::getValue).map(Object::toString).orElse(null);
                                        if(name != null && val != null)
                                        {
                                            result.put(name, val);
                                            continue;
                                        }
                                        // alternative shape: single-key map inside list element: - foo: bar
                                        Map<String, YAMLNode> children = node.getChildren();
                                        if(children.size() == 1)
                                        {
                                            Map.Entry<String, YAMLNode> entry = children.entrySet().iterator().next();
                                            String k = entry.getKey();
                                            String vv = entry.getValue().getValue().map(Object::toString).orElse(null);
                                            if(k != null && vv != null)
                                            {
                                                result.put(k, vv);
                                                continue;
                                            }
                                        }
                                        // As a last attempt, if the node itself holds a scalar value and has an identifying child key
                                        // (eg. node has children and a scalar value) - skip unless you have a clear mapping scheme.
                                    }*/
                                    // Element is a plain Map (defensive)
                                    /*else */if(elem instanceof Map<?, ?> mapElem)
                                    {
                                        for(Map.Entry<?, ?> me : mapElem.entrySet())
                                        {
                                            String k = String.valueOf(me.getKey());
                                            Object ov = me.getValue();
                                            result.put(k, ov == null ? null : ov.toString());
                                        }
                                    }
                                    // Element is scalar string like "foo=bar"
                                    /*else if(elem instanceof String s)
                                    {
                                        int eq = s.indexOf('=');
                                        if(eq > 0)
                                        {
                                            String k = s.substring(0, eq).trim();
                                            String vv = s.substring(eq + 1).trim();
                                            if(!k.isEmpty())
                                            {
                                                result.put(k, vv);
                                            }
                                        }
                                    }*/
                                    // other scalar forms are ignored
                                }
                            }
                            // Case: vars is a Map at top level
                            else if(v instanceof Map<?, ?> mapTop)
                            {
                                for(Map.Entry<?, ?> me : mapTop.entrySet())
                                {
                                    String k = String.valueOf(me.getKey());
                                    Object ov = me.getValue();
                                    result.put(k, ov == null ? null : ov.toString());
                                }
                            }
                            return Collections.unmodifiableMap(result);
                        })
                        .orElse(Collections.emptyMap());
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
