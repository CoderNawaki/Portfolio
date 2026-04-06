package com.codernawaki.portfolio;

import java.util.Map;

public record ContactResponse(
        boolean success,
        String message,
        Map<String, String> fieldErrors) {
}
