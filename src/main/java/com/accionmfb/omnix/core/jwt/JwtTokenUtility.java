package com.accionmfb.omnix.core.jwt;

import com.accionmfb.omnix.core.commons.OmnixParam;
import com.accionmfb.omnix.core.commons.ResponseCode;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.exception.OmnixApiException;
import com.accionmfb.omnix.core.jwt.props.DefaultJwtProperties;
import com.accionmfb.omnix.core.localsource.core.LocalParamStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.accionmfb.omnix.core.util.CommonUtil.cleanToken;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {DefaultJwtProperties.class})
public class JwtTokenUtility implements JwtTokenUtil{

    private final ObjectMapper objectMapper;
    private final LocalParamStorage localParamStorage;
    private final DefaultJwtProperties defaultJwtProperties;

    private static final String APP_USER_USERNAME_KEY = "O-apiId";
    private static final String APP_USER_CHANNEL_KEY = "O-channel";
    private static final String JWT_ISSUER = "Accion Microfinance Bank";
    private static final String APP_USER_TOKEN = "App user bearer token";
    private static final String USER_SESSION_TOKEN = "User session token";
    private static final String ADMIN_USER_TOKEN = "Admin User session token";
    private static final String USER_SESSION_TOKEN_ID = "O-sessionId";
    private static final String ADMIN_USER_EMAIL_KEY = "O-email";
    private static final String JWT_CRED_KEY = "X-AMFB-APP-USER-CRED-KEY";


    @Override
    public String generateAppUserToken(String apiId, String appUserChannel){
        String configuredExp = localParamStorage.getParamValueOrDefault(OmnixParam.APP_USER_TOKEN_EXPIRATION_IN_HR, defaultJwtProperties.getAppUserTokenInHr());
        Claims appTokenClaims = generateAppUserClaims(apiId, appUserChannel);
        return Jwts.builder()
                .setClaims(appTokenClaims)
                .setSubject(APP_USER_TOKEN)
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(Long.parseLong(configuredExp), ChronoUnit.HOURS)))
                .signWith(SignatureAlgorithm.HS256, defaultJwtProperties.getJwtKey())
                .compact();
    }

    @Override
    public String generateUserIdToken(String appUserChannel, String sessionId){
        String configuredExp = localParamStorage.getParamValueOrDefault(OmnixParam.ID_TOKEN_EXPIRATION_IN_MIN, defaultJwtProperties.getIdTokenExpiryInMin());
        Claims userTokenClaims = generateUserIdClaims(sessionId, appUserChannel);
        return Jwts.builder()
                .setClaims(userTokenClaims)
                .setSubject(USER_SESSION_TOKEN)
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(Long.parseLong(configuredExp), ChronoUnit.MINUTES)))
                .signWith(SignatureAlgorithm.HS256, defaultJwtProperties.getJwtKey())
                .compact();
    }

    @Override
    public String generateAdminUserToken(String appUserChannel, String emailAddress){
        String configuredExp = localParamStorage.getParamValueOrDefault(OmnixParam.ADMIN_USER_EXPIRATION_IN_MIN, defaultJwtProperties.getAdminUserTokenExpirationInMin());
        Claims adminTokenClaims = generateAdminUserClaims(emailAddress, appUserChannel);
        return Jwts.builder()
                .setClaims(adminTokenClaims)
                .setSubject(ADMIN_USER_TOKEN)
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(Long.parseLong(configuredExp), ChronoUnit.MINUTES)))
                .signWith(SignatureAlgorithm.HS256, defaultJwtProperties.getJwtKey())
                .compact();
    }

    @Override
    public String getApiIdFromToken(String token){
        return getClaimValueFromKey(APP_USER_USERNAME_KEY, cleanToken(token));
    }

    @Override
    public String getChannelFromToken(String token){
        return getClaimValueFromKey(APP_USER_CHANNEL_KEY, cleanToken(token));
    }

    @Override
    public String getUserSessionIdFromToken(String token){
        return getClaimValueFromKey(USER_SESSION_TOKEN_ID, cleanToken(token));
    }

    @Override
    public String getAdminUserEmailFromToken(String token){
        return getClaimValueFromKey(ADMIN_USER_EMAIL_KEY, cleanToken(token));
    }

    @Override
    public boolean isExpiredToken(String token){
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(defaultJwtProperties.getJwtKey())
                    .parseClaimsJws(cleanToken(token))
                    .getBody()
                    .getExpiration();
            return Objects.nonNull(expiration) && expiration.before(Date.from(Instant.now()));
        }catch (ExpiredJwtException exception){
            throw OmnixApiException.newInstance()
                    .withCode(ResponseCode.INVALID_CREDENTIALS)
                    .withStatusCode(HttpStatus.UNAUTHORIZED.value())
                    .withMessage("Bearer token expired");
        }
    }

    @Override
    public LocalDateTime getTokenIssuedDateTime(String token){
        return getClaimsFromToken(token).getIssuedAt().toInstant().atZone(ZoneId.of(StringValues.AFRICA_LAGOS_ZONE)).toLocalDateTime();
    }

    @Override
    public LocalDateTime getExpirationDateTimeFromToken(String token){
        return getClaimsFromToken(token).getExpiration().toInstant().atZone(ZoneId.of(StringValues.AFRICA_LAGOS_ZONE)).toLocalDateTime();
    }

    @SneakyThrows
    public String getClaimValueFromKey(String claimKey, String token){
        try {
            token = cleanToken(token);
            Claims claims = getClaimsFromToken(token);
            String base64CredentialKey = Base64.getEncoder().encodeToString(JWT_CRED_KEY.getBytes(StandardCharsets.UTF_8));
            String base64Credentials = (String) claims.get(base64CredentialKey);
            String bareCredentialJson = new String(Base64.getDecoder().decode(base64Credentials));
            Map<String, String> credentialMap = objectMapper.readValue(bareCredentialJson, new TypeReference<HashMap<String, String>>() {
            });
            String value = credentialMap.get(claimKey);
            return Objects.isNull(value) ? null : value;
        }catch (ExpiredJwtException exception){
            throw OmnixApiException.newInstance()
                    .withCode(ResponseCode.INVALID_CREDENTIALS)
                    .withMessage("Expired credentials");
        }
        catch (Exception exception){
            log.error("Exception occurred while trying to extract claim from JWT. Exception message is: {}", exception.getMessage());
           return null;
        }
    }

    public Claims getClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(defaultJwtProperties.getJwtKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims generateAppUserClaims(String apiId, String channel){
        return buildClaims(apiId, channel, APP_USER_TOKEN, TokenType.APP_USER_TOKEN);
    }

    private Claims generateUserIdClaims(String sessionId, String channel){
        return buildClaims(sessionId, channel, USER_SESSION_TOKEN, TokenType.USER_SESSION_TOKEN);
    }

    private Claims generateAdminUserClaims(String emailAddress, String channel){
        return buildClaims(emailAddress, channel, ADMIN_USER_TOKEN, TokenType.ADMIN_USER_TOKEN);
    }

    private Claims buildClaims(String keyValue, String channel, String subject, TokenType tokenType) {
        log.info("Login token type: {}", tokenType);
        String base64Credentials;
        switch (tokenType){
            case APP_USER_TOKEN : { base64Credentials = buildAppUserBase64EncodedJwtCredentials(keyValue, channel); break; }
            case ADMIN_USER_TOKEN : { base64Credentials = buildAdminUserSessionBase64EncodedJwtCredentials(keyValue, channel); break; }
            case USER_SESSION_TOKEN: { base64Credentials = buildUserSessionBase64EncodedJwtCredentials(keyValue, channel); break; }
            default : {
                throw new IllegalArgumentException("Invalid token type");
            }
        }

        Claims claims = Jwts.claims()
                .setAudience(channel)
                .setId(String.join(StringValues.STROKE, base64Credentials, UUID.randomUUID().toString()))
                .setIssuedAt(new Date())
                .setIssuer(JWT_ISSUER)
                .setSubject(subject);

        String base64CredentialKey = Base64.getEncoder().encodeToString(JWT_CRED_KEY.getBytes(StandardCharsets.UTF_8));
        claims.put(base64CredentialKey, base64Credentials);
        return claims;
    }

    private String buildAppUserBase64EncodedJwtCredentials(String apiId, String channel){
        return buildBase64EncodedJwtCredentials(APP_USER_USERNAME_KEY, apiId, channel);
    }

    private String buildUserSessionBase64EncodedJwtCredentials(String sessionId, String channel){
        return buildBase64EncodedJwtCredentials(USER_SESSION_TOKEN_ID, sessionId, channel);
    }

    private String buildAdminUserSessionBase64EncodedJwtCredentials(String emailAddress, String channel){
        return buildBase64EncodedJwtCredentials(ADMIN_USER_EMAIL_KEY, emailAddress, channel);
    }

    @SneakyThrows
    private String buildBase64EncodedJwtCredentials(String key, String value, String channel){
        Map<String, String> credentialMap = new HashMap<>();
        credentialMap.put(key, value);
        credentialMap.put(APP_USER_CHANNEL_KEY, channel);
        String credentialsJson = objectMapper.writeValueAsString(credentialMap);
        return Base64.getEncoder().encodeToString(credentialsJson.getBytes(StandardCharsets.UTF_8));
    }
}
