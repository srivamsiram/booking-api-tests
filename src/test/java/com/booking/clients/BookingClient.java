package com.booking.clients;

import com.booking.models.Booking;
import io.restassured.response.Response;

import java.util.Map;

public class BookingClient extends BaseClient {

    private static final String PING_ENDPOINT = "/ping";
    private static final String BOOKING_ENDPOINT = "/booking";
    private static final String BOOKING_BY_ID_ENDPOINT = "/booking/{id}";

    /** GET /ping - health check. Restful-Booker returns 201 when healthy. */
    public Response ping() {
        return request()
                .when()
                .get(PING_ENDPOINT);
    }

    /** POST /booking with a valid Booking object. */
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
    public Response createBookingWithRawPayload(Map<String, Object> payload) {
        return request()
                .body(payload)
                .when()
                .post(BOOKING_ENDPOINT);
    }

    /** GET /booking - returns all booking IDs. */
    public Response getBookingIds() {
        return request()
                .when()
                .get(BOOKING_ENDPOINT);
    }

    /** GET /booking?firstname=...&lastname=... - returns IDs matching the filters. */
    public Response getBookingIds(Map<String, ?> filters) {
        return request()
                .queryParams(filters)
                .when()
                .get(BOOKING_ENDPOINT);
    }

    /** GET /booking/{id} */
    public Response getBooking(int id) {
        return request()
                .pathParam("id", id)
                .when()
                .get(BOOKING_BY_ID_ENDPOINT);
    }

    /** PUT /booking/{id} - full update. Pass token = null to send no token. */
    public Response updateBooking(int id, Booking booking, String token) {
        return authorizedRequest(token)
                .pathParam("id", id)
                .body(booking)
                .when()
                .put(BOOKING_BY_ID_ENDPOINT);
    }

    /** PATCH /booking/{id} - partial update. Only non-null fields are sent. */
    public Response partialUpdateBooking(int id, Booking partialBooking, String token) {
        return authorizedRequest(token)
                .pathParam("id", id)
                .body(partialBooking)
                .when()
                .patch(BOOKING_BY_ID_ENDPOINT);
    }

    /** DELETE /booking/{id}. Pass token = null to send no token. */
    public Response deleteBooking(int id, String token) {
        return authorizedRequest(token)
                .pathParam("id", id)
                .when()
                .delete(BOOKING_BY_ID_ENDPOINT);
    }
}