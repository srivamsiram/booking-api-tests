package com.booking.clients;

import com.booking.models.Booking;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

public class BookingClient extends BaseClient {

    private static final String PING_ENDPOINT = "/ping";
    private static final String BOOKING_ENDPOINT = "/booking";
    private static final String BOOKING_BY_ID_ENDPOINT = "/booking/{id}";

    /** GET /ping - health check. Restful-Booker returns 201 when healthy. */
    @Step("Health check: GET /ping")
    public Response ping() {
        return request()
                .when()
                .get(PING_ENDPOINT);
    }

    /** POST /booking with a valid Booking object. */
    @Step("Create booking for {booking.firstname} {booking.lastname}")
    public Response createBooking(Booking booking) {
        return request()
                .body(booking)
                .when()
                .post(BOOKING_ENDPOINT);
    }

    /**
     * POST /booking with a free-form payload (e.g. totalprice = "abc").
     * Used by negative tests that need data a Booking object cannot hold.
     */
    @Step("Create booking with raw payload")
    public Response createBookingWithRawPayload(Map<String, Object> payload) {
        return request()
                .body(payload)
                .when()
                .post(BOOKING_ENDPOINT);
    }

    /** GET /booking - returns all booking IDs. */
    @Step("Get all booking IDs")
    public Response getBookingIds() {
        return request()
                .when()
                .get(BOOKING_ENDPOINT);
    }

    /** GET /booking?firstname=...&lastname=... - returns IDs matching the filters. */
    @Step("Get booking IDs filtered by {filters}")
    public Response getBookingIds(Map<String, ?> filters) {
        return request()
                .queryParams(filters)
                .when()
                .get(BOOKING_ENDPOINT);
    }

    /** GET /booking/{id} */
    @Step("Get booking #{id}")
    public Response getBooking(int id) {
        return request()
                .pathParam("id", id)
                .when()
                .get(BOOKING_BY_ID_ENDPOINT);
    }

    /** PUT /booking/{id} - full update. Pass token = null to send no token. */
    @Step("Update booking #{id} (PUT)")
    public Response updateBooking(int id, Booking booking, String token) {
        return authorizedRequest(token)
                .pathParam("id", id)
                .body(booking)
                .when()
                .put(BOOKING_BY_ID_ENDPOINT);
    }

    /** PATCH /booking/{id} - partial update. Only non-null fields are sent. */
    @Step("Partially update booking #{id} (PATCH)")
    public Response partialUpdateBooking(int id, Booking partialBooking, String token) {
        return authorizedRequest(token)
                .pathParam("id", id)
                .body(partialBooking)
                .when()
                .patch(BOOKING_BY_ID_ENDPOINT);
    }

    /** DELETE /booking/{id}. Pass token = null to send no token. */
    @Step("Delete booking #{id}")
    public Response deleteBooking(int id, String token) {
        return authorizedRequest(token)
                .pathParam("id", id)
                .when()
                .delete(BOOKING_BY_ID_ENDPOINT);
    }
}