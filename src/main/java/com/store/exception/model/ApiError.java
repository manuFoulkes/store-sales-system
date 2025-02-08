package com.store.exception.model;

import java.time.Instant;

public record ApiError(
        int statusCode,
        String error,
        String message,
        Instant timestamp
) {
}
