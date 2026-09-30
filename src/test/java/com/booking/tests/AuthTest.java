package com.booking.tests;

import com.booking.config.ConfigManager;
import com.booking.models.AuthRequest;
import com.booking.models.AuthResponse;
import com.booking.models.Booking;
import com.booking.utils.BookingBuilder;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test(groups = "regression")
public class AuthTest extends BaseTest {

    private static final String INVALID_TOKEN = "invalid-token-123";

    @Test(groups = "smoke", description = "Valid credentials return an auth token")
    public void validCredentialsShouldReturnToken() {
        AuthRequest credentials = new AuthRequest(ConfigManager.getUsername(), ConfigManager.getPassword());

        Response response = authClient.createToken(credentials);

        assertEquals(response.statusCode(), 200);
        AuthResponse body = response.as(AuthResponse.class);
        assertNotNull(body.getToken(), "Token should be returned");
        assertFalse(body.getToken().isBlank(), "Token should not be blank");
    }

    @Test(description = "Wrong password returns no token and a 'Bad credentials' reason")
    public void invalidPasswordShouldNotReturnToken() {
        AuthRequest credentials = new AuthRequest(ConfigManager.getUsername(), "wrong-password");

        AuthResponse body = authClient.createToken(credentials).as(AuthResponse.class);

        assertNull(body.getToken(), "No token should be issued for bad credentials");
        assertEquals(body.getReason(), "Bad credentials");
    }

    @Test(groups = "known-bug", description = "BUG: wrong password should return 401, API returns 200")
    public void invalidPasswordShouldReturn401() {
        AuthRequest credentials = new AuthRequest(ConfigManager.getUsername(), "wrong-password");

        Response response = authClient.createToken(credentials);

        assertEquals(response.statusCode(), 401);
    }

    @Test(description = "PUT without a token is rejected with 403")
    public void updateWithoutTokenShouldReturn403() {
        int bookingId = createBookingAndGetId(BookingBuilder.aValidBooking().build());

        Response response = bookingClient.updateBooking(bookingId, BookingBuilder.aValidBooking().build(), null);

        assertEquals(response.statusCode(), 403);
    }

    @Test(description = "PUT with an invalid token is rejected with 403")
    public void updateWithInvalidTokenShouldReturn403() {
        int bookingId = createBookingAndGetId(BookingBuilder.aValidBooking().build());

        Response response = bookingClient.updateBooking(bookingId, BookingBuilder.aValidBooking().build(), INVALID_TOKEN);

        assertEquals(response.statusCode(), 403);
    }

    @Test(description = "PATCH without a token is rejected with 403")
    public void partialUpdateWithoutTokenShouldReturn403() {
        int bookingId = createBookingAndGetId(BookingBuilder.aValidBooking().build());
        Booking patch = new Booking();
        patch.setFirstname("Hacker");

        Response response = bookingClient.partialUpdateBooking(bookingId, patch, null);

        assertEquals(response.statusCode(), 403);
    }

    @Test(description = "DELETE without a token is rejected and the booking still exists")
    public void deleteWithoutTokenShouldReturn403() {
        int bookingId = createBookingAndGetId(BookingBuilder.aValidBooking().build());

        Response response = bookingClient.deleteBooking(bookingId, null);

        assertEquals(response.statusCode(), 403);
        assertEquals(bookingClient.getBooking(bookingId).statusCode(), 200, "Booking must not be deleted");
    }

    @Test(description = "DELETE with an invalid token is rejected and the booking still exists")
    public void deleteWithInvalidTokenShouldReturn403() {
        int bookingId = createBookingAndGetId(BookingBuilder.aValidBooking().build());

        Response response = bookingClient.deleteBooking(bookingId, INVALID_TOKEN);

        assertEquals(response.statusCode(), 403);
        assertEquals(bookingClient.getBooking(bookingId).statusCode(), 200, "Booking must not be deleted");
    }
}
