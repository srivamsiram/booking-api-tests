package com.booking.clients;

import com.booking.config.ConfigManager;
import com.booking.models.AuthRequest;
import com.booking.models.AuthResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

public class AuthClient extends BaseClient {

    private static final String AUTH_ENDPOINT = "/auth";

    /** POST /auth with any credentials. Returns the raw response so tests can check anything. */
    @Step("Request auth token for user '{credentials.username}'")
    public Response createToken(AuthRequest credentials) {
        return request()
                .body(credentials)
                .when()
                .post(AUTH_ENDPOINT);
    }

    /** Convenience helper: logs in with the configured credentials and returns just the token. */
    @Step("Get a valid auth token")
    public String getValidToken() {
        AuthRequest credentials = new AuthRequest(ConfigManager.getUsername(), ConfigManager.getPassword());
        AuthResponse authResponse = createToken(credentials).as(AuthResponse.class);

        if (authResponse.getToken() == null) {
            throw new IllegalStateException("Could not get auth token: " + authResponse);
        }
        return authResponse.getToken();
    }
}