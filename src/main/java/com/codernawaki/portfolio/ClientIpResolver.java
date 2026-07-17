package com.codernawaki.portfolio;

import jakarta.servlet.http.HttpServletRequest;

final class ClientIpResolver {

    private ClientIpResolver() {
    }

    static String resolve(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String cfConnectingIp = request.getHeader("CF-Connecting-IP");
        if (cfConnectingIp != null && !cfConnectingIp.isBlank()) {
            return cfConnectingIp;
        }
        return request.getRemoteAddr();
    }
}
