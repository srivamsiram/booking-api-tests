package com.booking.tests;

import io.restassured.RestAssured;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

public class SetupVerificationTest {

    @Test
    public void apiShouldBeReachable() {
        RestAssured.given()
                .baseUri("https://restful-booker.herokuapp.com")
                .when()
                .get("/ping")
                .then()
                .statusCode(201);
    }
}