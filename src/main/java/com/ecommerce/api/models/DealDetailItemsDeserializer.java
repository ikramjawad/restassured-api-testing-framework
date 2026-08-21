package com.ecommerce.api.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The deal-detail endpoint's "items" field has two different real shapes depending on
 * environment: an array of items each carrying "retailer_links" (Staging), or a single
 * item object with its retailer links nested under a field literally named "items" (Live).
 * This normalizes both into a consistent List<DealDetailItem>.
 */
public class DealDetailItemsDeserializer extends JsonDeserializer<List<DealDetailItem>> {

    @Override
    public List<DealDetailItem> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);

        List<DealDetailItem> items = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode itemNode : node) {
                items.add(toDealDetailItem(mapper, itemNode));
            }
        } else if (node.isObject()) {
            items.add(toDealDetailItem(mapper, node));
        }
        return items;
    }

    private DealDetailItem toDealDetailItem(ObjectMapper mapper, JsonNode node) {
        DealDetailItem item = new DealDetailItem();
        item.setId(textOrNull(node, "id"));
        item.setName(textOrNull(node, "name"));
        item.setColor(textOrNull(node, "color"));
        item.setModel(textOrNull(node, "model"));

        List<String> upc = new ArrayList<>();
        JsonNode upcNode = node.get("upc");
        if (upcNode != null && upcNode.isArray()) {
            upcNode.forEach(n -> upc.add(n.asText()));
        }
        item.setUpc(upc);

        JsonNode linksNode = node.has("retailer_links") ? node.get("retailer_links") : node.get("items");
        List<RetailerLink> links = new ArrayList<>();
        if (linksNode != null && linksNode.isArray()) {
            for (JsonNode linkNode : linksNode) {
                links.add(mapper.convertValue(linkNode, RetailerLink.class));
            }
        }
        item.setRetailerLinks(links);

        return item;
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asText();
    }
}
