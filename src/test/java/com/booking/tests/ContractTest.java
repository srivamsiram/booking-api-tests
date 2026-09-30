package com.booking.tests;

import com.booking.config.ConfigManager;
import com.booking.models.AuthRequest;
import com.booking.utils.BookingBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Epic("Restful-Booker API")
@Feature("API contract")
@Story("JSON schema validation")
@Test(groups = "regression")
public class ContractTest extends BaseTest {

    @Test(description = "POST /booking response matches its JSON schema")
    public void createBookingResponseShouldMatchSchema() {
        bookingClient.createBooking(BookingBuilder.aValidBooking().build())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-response.json"));
    }

    @Test(description = "GET /booking/{id} response matches its JSON schema")
    public void getBookingResponseShouldMatchSchema() {
        int bookingId = createBookingAndGetId(BookingBuilder.aValidBooking().build());

        bookingClient.getBooking(bookingId)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking.json"));
    }

    @Test(description = "POST /auth response matches its JSON schema")
    public void authResponseShouldMatchSchema() {
        AuthRequest credentials = new AuthRequest(ConfigManager.getUsername(), ConfigManager.getPassword());

        authClient.createToken(credentials)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/auth-response.json"));
    }
}
