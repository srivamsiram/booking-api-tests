package com.booking.tests;

import com.booking.config.ConfigManager;
import com.booking.models.AuthRequest;
import com.booking.models.AuthResponse;
import com.booking.models.Booking;
import com.booking.models.BookingDates;
import com.booking.models.CreateBookingResponse;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * Temporary Phase 2 checks. Replaced by real clients + tests in Phase 3/4.
 */
public class SetupVerificationTest {

    @Test
    public void apiShouldBeReachable() {
        given()
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .get("/ping")
                .then()
                .statusCode(201);
    }

    @Test
    public void authRequestShouldReturnToken() {
        AuthRequest request = new AuthRequest(ConfigManager.getUsername(), ConfigManager.getPassword());

        AuthResponse response = given()
                .baseUri(ConfigManager.getBaseUrl())
                .contentType(ContentType.JSON)
                .body(request)                       // POJO -> JSON (Jackson)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);             // JSON -> POJO (Jackson)

        Assert.assertNotNull(response.getToken(), "Expected a token but got: " + response);
    }

    @Test
    public void createdBookingShouldMatchRequest() {
        Booking expected = new Booking("Jim", "Brown", 111, true,
                new BookingDates("2026-01-01", "2026-01-05"), "Breakfast");

        CreateBookingResponse response = given()
                .baseUri(ConfigManager.getBaseUrl())
                .contentType(ContentType.JSON)
                .accept("application/json")          // exact value; ContentType.JSON sends 4 types -> 418
                .body(expected)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract()
                .as(CreateBookingResponse.class);

        Assert.assertNotNull(response.getBookingid(), "Booking ID should be generated");
        Assert.assertEquals(response.getBooking(), expected);   // uses equals()
    }
}