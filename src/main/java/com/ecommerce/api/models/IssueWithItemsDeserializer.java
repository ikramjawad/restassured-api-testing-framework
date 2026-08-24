package com.ecommerce.api.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * issue_with_item comes back as an array of entries, a single entry object, or null/[] when
 * a shipment has nothing to report. This normalizes all three into a List<IssueWithItem>.
 */
public class IssueWithItemsDeserializer extends JsonDeserializer<List<IssueWithItem>> {

    @Override
    public List<IssueWithItem> getNullValue(DeserializationContext ctxt) {
        return Collections.emptyList();
    }

    @Override
    public List<IssueWithItem> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);

        if (node == null || node.isNull()) {
            return Collections.emptyList();
        }

        List<IssueWithItem> items = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode itemNode : node) {
                items.add(mapper.convertValue(itemNode, IssueWithItem.class));
            }
        } else if (node.isObject()) {
            items.add(mapper.convertValue(node, IssueWithItem.class));
        }
        return items;
    }
}
