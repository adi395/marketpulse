package com.marketpulse.backend.util;

import java.util.regex.Pattern;

/** Validation for the client-supplied X-User-Id identifier. */
public final class UserIds {

    private static final Pattern VALID = Pattern.compile("^[A-Za-z0-9_-]{1,64}$");

    private UserIds() {}

    /**
     * Returns the user id if well-formed, else throws {@link IllegalArgumentException}
     * (mapped to HTTP 400). Note: this identifies a client but does not authenticate
     * one — it is tenant scoping, not auth.
     */
    public static String require(String userId) {
        if (userId == null || !VALID.matcher(userId).matches()) {
            throw new IllegalArgumentException("Invalid or missing X-User-Id");
        }
        return userId;
    }
}
