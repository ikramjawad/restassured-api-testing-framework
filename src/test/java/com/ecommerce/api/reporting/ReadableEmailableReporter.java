package com.ecommerce.api.reporting;

import org.testng.reporters.EmailableReporter2;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * TestNG's emailable-report.html, with the "Start" column shown as a date and time
 * (e.g. 2026-11-19 19:00:16 PKT) instead of raw epoch milliseconds.
 *
 * Registered as a listener in pom.xml, which also turns off TestNG's default emailable reporter
 * (noEmailableReporter) so the two don't both write emailable-report.html.
 */
public class ReadableEmailableReporter extends EmailableReporter2 {

    // pom.xml passes report.timezone (Pakistan time) so a CI agent running in UTC still shows PKT.
    private static final DateTimeFormatter START_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
                    .withZone(ZoneId.of(System.getProperty("report.timezone", "Asia/Karachi")));

    @Override
    protected String getFormattedStartTime(long startMillis) {
        return START_TIME_FORMAT.format(Instant.ofEpochMilli(startMillis));
    }
}
