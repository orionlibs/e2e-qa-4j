package com.yapily.e2eqa4j.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//@ToString
public class YAMLNode
{
    private final Map<String, YAMLNode> children = new LinkedHashMap<>();
    private Object value;


    public YAMLNode()
    {
    }


    public YAMLNode(Object value)
    {
        this.value = value;
    }


    @JsonAnyGetter
    public Map<String, YAMLNode> getChildren()
    {
        return children;
    }


    @JsonAnySetter
    public void set(String key, Object val)
    {
        if(val instanceof Map<?, ?>)
        {
            children.put(key, fromMap((Map<?, ?>)val));
        }
        else if(val instanceof List<?>)
        {
            // convert list elements to YamlNode where element is a map
            List<?> list = (List<?>)val;
            List<Object> converted = new ArrayList<>(list.size());
            for(Object elem : list)
            {
                if(elem instanceof Map<?, ?>)
                {
                    converted.add(fromMap((Map<?, ?>)elem));
                }
                else
                {
                    converted.add(elem);
                }
            }
            children.put(key, new YAMLNode(converted));
            value = converted;
        }
        else
        {
            // scalar (string, number, boolean, null, etc.)
            children.put(key, new YAMLNode(val));
            value = val;
        }
    }


    public Optional<Object> getByDottedPath(String path)
    {
        String[] parts = path.split("\\.");
        YAMLNode current = this;
        for(String part : parts)
        {
            // ---- Case 1: part is a number => list index access
            if(part.matches("\\d+"))
            {
                int index = Integer.parseInt(part);
                // ensure current is a leaf holding a List
                Optional<Object> valueOpt = current.getValue();
                if(valueOpt.isEmpty())
                {
                    return Optional.empty();
                }
                Object value = valueOpt.get();
                if(!(value instanceof List<?> list))
                {
                    return Optional.empty();
                }
                if(index < 0 || index >= list.size())
                {
                    return Optional.empty();
                }
                Object element = list.get(index);
                // element may be a YamlNode (map) or scalar
                if(element instanceof YAMLNode yn)
                {
                    current = yn;
                }
                else
                {
                    // scalar leaf reached; if this is not the last part => fail
                    return (part.equals(parts[parts.length - 1]))
                                    ? Optional.of(element)
                                    : Optional.empty();
                }
            }
            // ---- Case 2: part is a normal map key
            else
            {
                current = current.getChild(part).orElse(null);
                if(current == null)
                {
                    return Optional.empty();
                }
            }
        }
        return current.getValue();
    }


    private static YAMLNode fromMap(Map<?, ?> map)
    {
        YAMLNode node = new YAMLNode();
        for(Map.Entry<?, ?> e : map.entrySet())
        {
            String k = String.valueOf(e.getKey());
            Object v = e.getValue();
            // use set(...) to handle nested maps/lists/scalars uniformly
            node.set(k, v);
        }
        return node;
    }


    @JsonIgnore
    public boolean isLeaf()
    {
        return children.isEmpty() && value != null;
    }


    @JsonIgnore
    public Optional<Object> getValue()
    {
        return Optional.ofNullable(value);
    }


    @JsonIgnore
    public Optional<YAMLNode> getChild(String key)
    {
        return Optional.ofNullable(children.get(key));
    }


    // Helper: get nested node by path: e.g. ["alpha","beta","gamma"]
    public Optional<YAMLNode> getByPath(String... path)
    {
        YAMLNode cur = this;
        for(String p : path)
        {
            if(cur == null)
            {
                return Optional.empty();
            }
            cur = cur.children.get(p);
        }
        return Optional.ofNullable(cur);
    }
}
