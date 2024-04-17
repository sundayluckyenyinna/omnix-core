package com.accionmfb.omnix.core.jwt;


import java.time.LocalDateTime;

public interface JwtTokenUtil {
    String generateAppUserToken(String apiId, String appUserChannel);

    String generateUserIdToken(String appUserChannel, String sessionId);

    String generateAdminUserToken(String appUserChannel, String emailAddress);

    String getApiIdFromToken(String token);

    String getChannelFromToken(String token);

    String getUserSessionIdFromToken(String token);

    String getAdminUserEmailFromToken(String token);

    boolean isExpiredToken(String token);

    LocalDateTime getTokenIssuedDateTime(String token);

    LocalDateTime getExpirationDateTimeFromToken(String token);
}
