package com.ecommerce.api.reporting;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.MultiPartSpecification;
import org.testng.Reporter;

import java.util.Map;

/**
 * Writes every API call (method, URL, request body, status and response body) into the TestNG
 * report via Reporter.log, so each step in emailable-report.html / index.html shows what the API
 * returned - for passed steps too, not only failures.
 *
 * Headers are deliberately left out so the API key/secret never reach the report. Bodies longer
 * than MAX_BODY_CHARS are cut; the full text is still in the surefire XML (TEST-*.xml).
 */
public class ReportLoggingFilter implements Filter {

    private static final int MAX_BODY_CHARS = 3000;

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {
        Response response = context.next(request, responseSpec);

        StringBuilder html = new StringBuilder()
                .append("<b>").append(escape(request.getMethod() + " " + request.getURI())).append("</b>");

        String requestBody = describeRequestBody(request);
        if (!requestBody.isEmpty()) {
            html.append(pre("Request:\n" + truncate(requestBody)));
        }
        html.append(pre("Response: " + response.getStatusLine() + "\n" + truncate(response.getBody().asPrettyString())));

        Reporter.log(html.toString());
        return response;
    }

    private static String describeRequestBody(FilterableRequestSpecification request) {
        StringBuilder body = new StringBuilder();
        Object rawBody = request.getBody();
        if (rawBody != null) {
            body.append(rawBody);
        }
        for (Map.Entry<String, ?> param : request.getFormParams().entrySet()) {
            body.append(param.getKey()).append(" = ").append(param.getValue()).append('\n');
        }
        for (MultiPartSpecification part : request.getMultiPartParams()) {
            body.append(part.getControlName()).append(" = ").append(part.getContent()).append('\n');
        }
        return body.toString().trim();
    }

    private static String truncate(String text) {
        if (text == null) {
            return "";
        }
        if (text.length() <= MAX_BODY_CHARS) {
            return text;
        }
        return text.substring(0, MAX_BODY_CHARS)
                + "\n... (cut - " + text.length() + " chars in total, full body in TEST-*.xml)";
    }

    private static String pre(String text) {
        return "<pre style=\"white-space:pre-wrap\">" + escape(text) + "</pre>";
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
