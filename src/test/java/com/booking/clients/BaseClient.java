package com.booking.clients;

import com.booking.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Parent of every API client. Holds the settings shared by ALL requests,
 * so they are defined once instead of repeated in every call.
 */
public abstract class BaseClient {

    private static final RequestSpecification BASE_SPEC = new RequestSpecBuilder()
            .setBaseUri(ConfigManager.getBaseUrl())
            .setContentType(ContentType.JSON)
            .setAccept("application/json")        // exact value; ContentType.JSON causes 418 on this API
            .addFilter(new AllureRestAssured())   // attaches request/response to Allure (customised in Phase 5)
            .build();

    /** Starts a new request that already has the base URL, headers and filters applied. */
    protected RequestSpecification request() {
        return given().spec(BASE_SPEC);
    }

    /** Same as request(), plus the auth token cookie (skipped when token is null). */
    protected RequestSpecification authorizedRequest(String token) {
        RequestSpecification spec = request();
        if (token != null) {
            spec.cookie("token", token);
        }
        return spec;
    }
}
