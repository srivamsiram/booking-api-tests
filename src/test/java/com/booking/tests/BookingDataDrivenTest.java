package com.booking.tests;

import com.booking.models.Booking;
import com.booking.models.CreateBookingResponse;
import com.booking.utils.BookingBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Epic("Restful-Booker API")
@Feature("Bookings")
@Story("Boundary values (data-driven)")
@Test(groups = "regression")
public class BookingDataDrivenTest extends BaseTest {

    /** Boundary / edge-case bookings that the API SHOULD accept. One row = one test run. */
    @DataProvider(name = "validBoundaryBookings")
    public Object[][] validBoundaryBookings() {
        LocalDate today = LocalDate.now();

        return new Object[][]{
                {"zero total price",
                        BookingBuilder.aValidBooking().withTotalprice(0).build()},
                {"large total price",
                        BookingBuilder.aValidBooking().withTotalprice(1_000_000).build()},
                {"single-character names",
                        BookingBuilder.aValidBooking().withFirstname("A").withLastname("B").build()},
                {"accented unicode names",
                        BookingBuilder.aValidBooking().withFirstname("JosÃ©").withLastname("MÃ¼ller").build()},
                {"deposit not paid",
                        BookingBuilder.aValidBooking().withDepositpaid(false).build()},
                {"no additional needs",
                        BookingBuilder.aValidBooking().withAdditionalneeds(null).build()},
                {"same-day check-in and check-out",
                        BookingBuilder.aValidBooking()
                                .withCheckin(today.plusDays(3).toString())
                                .withCheckout(today.plusDays(3).toString()).build()},
                {"leap-day check-in",
                        BookingBuilder.aValidBooking()
                                .withCheckin("2028-02-29")
                                .withCheckout("2028-03-01").build()},
        };
    }

    @Test(dataProvider = "validBoundaryBookings",
            description = "Boundary bookings are created and stored exactly as sent")
    public void boundaryBookingShouldBeCreated(String scenario, Booking booking) {
        Response response = bookingClient.createBooking(booking);

        assertEquals(response.statusCode(), 200, "Scenario: " + scenario);
        CreateBookingResponse body = response.as(CreateBookingResponse.class);
        assertNotNull(body.getBookingid(), "Scenario: " + scenario);
        assertEquals(body.getBooking(), booking, "Scenario: " + scenario);
    }
}
