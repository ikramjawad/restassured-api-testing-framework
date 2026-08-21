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
 * reservation_list is an array of reservations when any exist, but the API returns the
 * plain string "No reservations available" instead of an empty array when there are none.
 */
public class ReservationListDeserializer extends JsonDeserializer<List<Reservation>> {

    @Override
    public List<Reservation> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);

        if (!node.isArray()) {
            return Collections.emptyList();
        }

        return mapper.convertValue(node, mapper.getTypeFactory().constructCollectionType(List.class, Reservation.class));
    }
}
