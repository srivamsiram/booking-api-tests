package com.booking.utils;

import com.booking.models.Booking;
import com.booking.models.BookingDates;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Creates Booking test data with sensible defaults.
 * Tests only change the fields they care about:
 *
 *   Booking booking = BookingBuilder.aValidBooking().withFirstname(null).build();
 */
public class BookingBuilder {

    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private String checkin;
    private String checkout;
    private String additionalneeds;

    private BookingBuilder() {
        // Use aValidBooking() instead
    }

    /** A booking that the API should accept. Names are unique per call. */
    public static BookingBuilder aValidBooking() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6);
        LocalDate today = LocalDate.now();

        BookingBuilder builder = new BookingBuilder();
        builder.firstname = "Test" + uniqueSuffix;
        builder.lastname = "User" + uniqueSuffix;
        builder.totalprice = 150;
        builder.depositpaid = true;
        builder.checkin = today.plusDays(7).toString();    // yyyy-MM-dd
        builder.checkout = today.plusDays(10).toString();
        builder.additionalneeds = "Breakfast";
        return builder;
    }

    public BookingBuilder withFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public BookingBuilder withLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public BookingBuilder withTotalprice(Integer totalprice) {
        this.totalprice = totalprice;
        return this;
    }

    public BookingBuilder withDepositpaid(Boolean depositpaid) {
        this.depositpaid = depositpaid;
        return this;
    }

    public BookingBuilder withCheckin(String checkin) {
        this.checkin = checkin;
        return this;
    }

    public BookingBuilder withCheckout(String checkout) {
        this.checkout = checkout;
        return this;
    }

    public BookingBuilder withAdditionalneeds(String additionalneeds) {
        this.additionalneeds = additionalneeds;
        return this;
    }

    public Booking build() {
        BookingDates dates = (checkin == null && checkout == null)
                ? null
                : new BookingDates(checkin, checkout);
        return new Booking(firstname, lastname, totalprice, depositpaid, dates, additionalneeds);
    }
}