package com.qa.blocrecon.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.blocrecon.records.EventRuleHierarchiesPageDTO;
import java.io.InputStream;

public class JsonDataReader {

    private static JsonNode rootNode;

    static {
        try {
            InputStream is = JsonDataReader.class
                    .getClassLoader()
                    .getResourceAsStream("metadata.json");
            ObjectMapper mapper = new ObjectMapper();
            rootNode = mapper.readTree(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EventRuleHierarchiesPageDTO getEventRuleHierarchiesPageData() {
        try {
            return new ObjectMapper()
                    .treeToValue(rootNode.get("eventRuleHierarchiesPage"), EventRuleHierarchiesPageDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to map loginPage data", e);
        }
    }
}
