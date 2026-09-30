package com.booking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Success: {"token": "abc123"}
 * Failure: {"reason": "Bad credentials"}  (returned with HTTP 200, not 401!)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {

    private String token;
    private String reason;

    public AuthResponse() {
        // Required by Jackson
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "AuthResponse{token='" + token + "', reason='" + reason + "'}";
    }
}
