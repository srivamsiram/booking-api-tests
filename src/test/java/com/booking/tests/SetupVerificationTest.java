package com.booking.tests;

import com.booking.clients.AuthClient;
import com.booking.clients.BookingClient;
import com.booking.models.Booking;
import com.booking.models.CreateBookingResponse;
import com.booking.utils.BookingBuilder;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Temporary Phase 3 check: proves every client method works end to end.
 * Replaced by real test classes in Phase 4.
 */
public class SetupVerificationTest {

    private final BookingClient bookingClient = new BookingClient();
    private final AuthClient authClient = new AuthClient();

    @Test
    public void apiShouldBeReachable() {
        bookingClient.ping().then().statusCode(201);
    }

    @Test
    public void authClientShouldReturnToken() {
        String token = authClient.getValidToken();
        Assert.assertFalse(token.isBlank(), "Token should not be blank");
    }

    @Test
    public void fullLifecycleShouldWorkThroughClients() {
        String token = authClient.getValidToken();

        // Create
        Booking newBooking = BookingBuilder.aValidBooking().build();
        Response createResponse = bookingClient.createBooking(newBooking);
        Assert.assertEquals(createResponse.statusCode(), 200);
        int bookingId = createResponse.as(CreateBookingResponse.class).getBookingid();

        // Get
        Response getResponse = bookingClient.getBooking(bookingId);
        Assert.assertEquals(getResponse.statusCode(), 200);
        Assert.assertEquals(getResponse.as(Booking.class), newBooking);

        // Update (PUT)
        Booking updated = BookingBuilder.aValidBooking().withTotalprice(999).build();
        Response updateResponse = bookingClient.updateBooking(bookingId, updated, token);
        Assert.assertEquals(updateResponse.statusCode(), 200);
        Assert.assertEquals(updateResponse.as(Booking.class), updated);

        // Partial update (PATCH) - only firstname is sent
        Booking partial = new Booking();
        partial.setFirstname("Patched");
        Response patchResponse = bookingClient.partialUpdateBooking(bookingId, partial, token);
        Assert.assertEquals(patchResponse.statusCode(), 200);
        Assert.assertEquals(patchResponse.as(Booking.class).getFirstname(), "Patched");

        // Delete, then confirm it is gone
        Assert.assertEquals(bookingClient.deleteBooking(bookingId, token).statusCode(), 201);
        Assert.assertEquals(bookingClient.getBooking(bookingId).statusCode(), 404);
    }
}