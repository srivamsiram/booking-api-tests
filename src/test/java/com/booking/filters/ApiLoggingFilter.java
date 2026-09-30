package com.booking.filters;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Runs on EVERY request sent through BaseClient:
 *   1. Attaches the full request and response to the Allure report.
 *   2. Prints a one-line summary to the console (handy in CI logs).
 */
public class ApiLoggingFilter implements Filter {

    private final AllureRestAssured allureFilter = new AllureRestAssured()
            .setRequestAttachmentName("Request")
            .setResponseAttachmentName("Response");

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {
        // AllureRestAssured sends the request (via context.next) and records both sides
        Response response = allureFilter.filter(requestSpec, responseSpec, context);

        System.out.printf("[API] %-6s %s -> %d (%d ms)%n",
                requestSpec.getMethod(), requestSpec.getURI(), response.statusCode(), response.time());
        return response;
    }
}
