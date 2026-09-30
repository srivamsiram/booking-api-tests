package com.booking.tests;

import com.booking.utils.BookingBuilder;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

@Test(groups = "regression")
public class NegativeBookingTest extends BaseTest {

    private static final int NON_EXISTENT_ID = 999_999_999;

    @Test(description = "GET a non-existent booking returns 404")
    public void getNonExistentBookingShouldReturn404() {
        Response response = bookingClient.getBooking(NON_EXISTENT_ID);

        assertEquals(response.statusCode(), 404);
    }

    @Test(description = "PUT on a non-existent booking returns 405")
    public void updateNonExistentBookingShouldReturn405() {
        Response response = bookingClient.updateBooking(NON_EXISTENT_ID, BookingBuilder.aValidBooking().build(), token);

        assertEquals(response.statusCode(), 405);
    }

    @Test(description = "DELETE on a non-existent booking returns 405")
    public void deleteNonExistentBookingShouldReturn405() {
        Response response = bookingClient.deleteBooking(NON_EXISTENT_ID, token);

        assertEquals(response.statusCode(), 405);
    }

    /**
     * Payloads a well-behaved API should reject with 400 Bad Request.
     * Restful-Booker accepts most of them (200) or crashes (500) - see README.
     */
    @DataProvider(name = "invalidPayloads")
    public Object[][] invalidPayloads() {
        Map<String, Object> missingFirstname = validPayload();
        missingFirstname.remove("firstname");

        Map<String, Object> missingDates = validPayload();
        missingDates.remove("bookingdates");

        Map<String, Object> priceAsText = validPayload();
        priceAsText.put("totalprice", "abc");

        Map<String, Object> firstnameAsNumber = validPayload();
        firstnameAsNumber.put("firstname", 12345);

        Map<String, Object> negativePrice = validPayload();
        negativePrice.put("totalprice", -50);

        Map<String, Object> invalidDate = validPayload();
        invalidDate.put("bookingdates", Map.of("checkin", "2026-13-45", "checkout", "not-a-date"));

        Map<String, Object> checkoutBeforeCheckin = validPayload();
        checkoutBeforeCheckin.put("bookingdates", Map.of(
                "checkin", LocalDate.now().plusDays(10).toString(),
                "checkout", LocalDate.now().plusDays(5).toString()));

        return new Object[][]{
                {"missing firstname", missingFirstname},
                {"missing bookingdates", missingDates},
                {"totalprice is text", priceAsText},
                {"firstname is a number", firstnameAsNumber},
                {"negative totalprice", negativePrice},
                {"invalid date format", invalidDate},
                {"checkout before checkin", checkoutBeforeCheckin},
        };
    }

    @Test(dataProvider = "invalidPayloads", groups = "known-bug",
            description = "BUG: invalid booking payloads should be rejected with 400")
    public void invalidPayloadShouldReturn400(String scenario, Map<String, Object> payload) {
        Response response = bookingClient.createBookingWithRawPayload(payload);

        assertEquals(response.statusCode(), 400, "Scenario: " + scenario);
    }

    private Map<String, Object> validPayload() {
        Map<String, Object> dates = new HashMap<>();
        dates.put("checkin", LocalDate.now().plusDays(7).toString());
        dates.put("checkout", LocalDate.now().plusDays(10).toString());

        Map<String, Object> payload = new HashMap<>();
        payload.put("firstname", "Negative");
        payload.put("lastname", "Tester");
        payload.put("totalprice", 100);
        payload.put("depositpaid", true);
        payload.put("bookingdates", dates);
        payload.put("additionalneeds", "None");
        return payload;
    }
}