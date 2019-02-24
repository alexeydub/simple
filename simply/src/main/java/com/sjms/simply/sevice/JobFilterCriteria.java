package com.sjms.simply.sevice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.sjms.simply.controller.ParameterUtils;
import com.sjms.simply.domain.Job;
import com.sjms.simply.domain.QueueJob;

/**
 * Filters for {@link Job} and {@link QueueJob}
 *
 * @param <T> Job or QueueJob
 */
public class JobFilterCriteria<T> {

    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String DESC = "description";

    private Map<String, String> allFilters = new HashMap<>(3);

    /**
     * Create a filter.
     * 
     * @param name job's name
     * @param tags job's tags
     * @param desc job's description
     */
    public JobFilterCriteria(String name, String tags, String desc) {
        if (!ParameterUtils.isEmpty(name)) {
            allFilters.put(NAME, name);
        }
        if (!ParameterUtils.isEmpty(tags)) {
            allFilters.put(TAGS, tags);
        }
        if (!ParameterUtils.isEmpty(desc)) {
            allFilters.put(DESC, desc);
        }
    }

    /**
     * @return all registered filters as strings
     */
    public Map<String, String> getAllFilters() {
        return allFilters;
    }

    /**
     * @return all registered filters as {@link Specification} list
     */
    public List<Specification<T>> getAllSpecs() {
        List<Specification<T>> result = new LinkedList<>();
        allFilters.entrySet().forEach(entry -> {
            Specification<T> r = (root, query, cb) -> {
                return cb.like(root.get("jc").get(entry.getKey()),
                        "%" + entry.getValue() + "%");
            };
            result.add(r);
        });
        return result;
    }
}
