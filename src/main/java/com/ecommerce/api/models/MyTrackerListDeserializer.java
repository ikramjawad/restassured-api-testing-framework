package com.ecommerce.api.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * my_tracker is an array of tracker rows when any exist, but the API returns the plain
 * string "No record found" instead of an empty array for a user with no reservations.
 */
public class MyTrackerListDeserializer extends JsonDeserializer<List<MyTrackerItem>> {

    @Override
    public List<MyTrackerItem> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);

        if (!node.isArray()) {
            return Collections.emptyList();
        }

        return mapper.convertValue(node, mapper.getTypeFactory().constructCollectionType(List.class, MyTrackerItem.class));
    }
}
