package com.booking.tests;

import com.booking.models.Booking;
import com.booking.models.CreateBookingResponse;
import com.booking.utils.BookingBuilder;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "regression")
public class BookingCrudTest extends BaseTest {

    @Test(groups = "smoke", description = "Create a booking and receive a new booking ID")
    public void createBookingShouldReturnNewId() {
        Booking booking = BookingBuilder.aValidBooking().build();

        Response response = bookingClient.createBooking(booking);

        assertEquals(response.statusCode(), 200);
        CreateBookingResponse body = response.as(CreateBookingResponse.class);
        assertNotNull(body.getBookingid(), "Booking ID should be generated");
        assertEquals(body.getBooking(), booking, "Response should echo the created booking");
    }

    @Test(groups = "smoke", description = "Get an existing booking by ID")
    public void getBookingByIdShouldReturnBooking() {
        Booking booking = BookingBuilder.aValidBooking().build();
        int bookingId = createBookingAndGetId(booking);

        Response response = bookingClient.getBooking(bookingId);

        assertEquals(response.statusCode(), 200);
        assertEquals(response.as(Booking.class), booking);
    }

    @Test(description = "Get all booking IDs returns a non-empty list")
    public void getAllBookingIdsShouldReturnList() {
        Response response = bookingClient.getBookingIds();

        assertEquals(response.statusCode(), 200);
        List<Integer> ids = response.jsonPath().getList("bookingid", Integer.class);
        assertFalse(ids.isEmpty(), "Expected at least one booking ID");
    }

    @Test(description = "Filter booking IDs by firstname and lastname")
    public void filterByNameShouldReturnCreatedBooking() {
        Booking booking = BookingBuilder.aValidBooking().build();
        int bookingId = createBookingAndGetId(booking);

        Response response = bookingClient.getBookingIds(Map.of(
                "firstname", booking.getFirstname(),
                "lastname", booking.getLastname()));

        assertEquals(response.statusCode(), 200);
        List<Integer> ids = response.jsonPath().getList("bookingid", Integer.class);
        assertTrue(ids.contains(bookingId), "Filtered IDs " + ids + " should contain " + bookingId);
    }

    @Test(groups = "smoke", description = "Fully update a booking with PUT")
    public void updateBookingShouldReplaceAllFields() {
        int bookingId = createBookingAndGetId(BookingBuilder.aValidBooking().build());
        Booking updated = BookingBuilder.aValidBooking()
                .withTotalprice(999)
                .withDepositpaid(false)
                .withAdditionalneeds("Late checkout")
                .build();

        Response response = bookingClient.updateBooking(bookingId, updated, token);

        assertEquals(response.statusCode(), 200);
        assertEquals(response.as(Booking.class), updated);
        assertEquals(bookingClient.getBooking(bookingId).as(Booking.class), updated,
                "GET after PUT should return the updated booking");
    }

    @Test(description = "Partially update a booking with PATCH; other fields stay unchanged")
    public void partialUpdateShouldChangeOnlyGivenFields() {
        Booking original = BookingBuilder.aValidBooking().build();
        int bookingId = createBookingAndGetId(original);
        Booking patch = new Booking();
        patch.setFirstname("Patched");
        patch.setTotalprice(42);

        Response response = bookingClient.partialUpdateBooking(bookingId, patch, token);

        assertEquals(response.statusCode(), 200);
        Booking result = response.as(Booking.class);
        assertEquals(result.getFirstname(), "Patched");
        assertEquals(result.getTotalprice(), Integer.valueOf(42));
        assertEquals(result.getLastname(), original.getLastname(), "Unpatched field must not change");
        assertEquals(result.getBookingdates(), original.getBookingdates(), "Unpatched field must not change");
    }

    @Test(groups = "smoke", description = "Delete a booking; it can no longer be fetched")
    public void deleteBookingShouldRemoveIt() {
        int bookingId = createBookingAndGetId(BookingBuilder.aValidBooking().build());

        Response response = bookingClient.deleteBooking(bookingId, token);

        // API returns 201 Created for a delete (see README: Deliberate Bugs Discovered)
        assertEquals(response.statusCode(), 201);
        assertEquals(bookingClient.getBooking(bookingId).statusCode(), 404);
    }
}
