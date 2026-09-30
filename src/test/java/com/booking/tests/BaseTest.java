package com.booking.tests;

import com.booking.clients.AuthClient;
import com.booking.clients.BookingClient;
import com.booking.models.Booking;
import com.booking.models.CreateBookingResponse;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

/**
 * Parent of all test classes: shared clients, auth token and setup helpers.
 */
public abstract class BaseTest {

    protected final BookingClient bookingClient = new BookingClient();
    protected final AuthClient authClient = new AuthClient();
    protected String token;

    // alwaysRun = true: without it, TestNG SKIPS this setup when you run with -Dgroups=smoke
    @BeforeClass(alwaysRun = true)
    public void fetchAuthToken() {
        token = authClient.getValidToken();
    }

    /** Test setup helper: creates a booking and returns its ID (fails fast if creation breaks). */
    protected int createBookingAndGetId(Booking booking) {
        Response response = bookingClient.createBooking(booking);
        Assert.assertEquals(response.statusCode(), 200, "Precondition failed: could not create booking");
        return response.as(CreateBookingResponse.class).getBookingid();
    }
}
