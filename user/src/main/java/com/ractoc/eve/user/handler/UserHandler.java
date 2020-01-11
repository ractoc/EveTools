package com.ractoc.eve.user.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.ractoc.eve.domain.user.UserModel;
import com.ractoc.eve.user.db.user.eve_user.user.User;
import com.ractoc.eve.user.db.user.eve_user.user.UserImpl;
import com.ractoc.eve.user.model.AccessDeniedException;
import com.ractoc.eve.user.model.EveJwtContent;
import com.ractoc.eve.user.model.OAuthToken;
import com.ractoc.eve.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class UserHandler {

    private final UserService service;

    @Autowired
    public UserHandler(UserService service) {
        this.service = service;
    }

    public String initiateLogin(String remoteIP) {
        return service.initializeUser(remoteIP);
    }

    public String getRefreshTokenForState(String eveState) {
        return getUser(eveState)
                .map(User::getRefreshToken)
                .orElseThrow(() -> new AccessDeniedException(eveState))
                .orElseThrow(() -> new AccessDeniedException(eveState));
    }

    public void storeEveUserRegistration(String eveState, OAuthToken oAuthToken, String remoteIp) {
        service.updateUser(convertOAuthTokenToUser(eveState, oAuthToken, remoteIp));
    }

    public String getValidIpByState(String eveState) {
        return getUser(eveState)
                .map(User::getIpAddress)
                .orElseThrow(() -> new AccessDeniedException(eveState));
    }

    public UserModel getUserByState(String eveState) {
        return getUser(eveState)
                .map(user ->
                        UserModel.builder()
                                .charId(user.getCharacterId().orElseThrow(() -> new AccessDeniedException(eveState)))
                                .characterName(user.getName().orElseThrow(() -> new AccessDeniedException(eveState)))
                                .eveState(eveState)
                                .ipAddress(user.getIpAddress())
                                .expiresAt(user.getLastRefresh().orElseThrow(() -> new AccessDeniedException(eveState))
                                        .toInstant(ZoneOffset.UTC).toEpochMilli() +
                                        user.getExpiresIn().orElseThrow(() -> new AccessDeniedException(eveState)))
                                .build())
                .orElseThrow(() -> new AccessDeniedException(eveState));
    }

    public UserModel getUserNameByState(String eveState) {
        return getUser(eveState)
                .map(user ->
                        UserModel.builder()
                                .characterName(user.getName().orElseThrow(() -> new AccessDeniedException(eveState)))
                                .eveState(eveState)
                                .build())
                .orElseThrow(() -> new AccessDeniedException(eveState));

    }

    private Optional<User> getUser(String eveState) {
        return service.getUser(eveState);
    }

    private int extractCharacterIdFromSub(String sub) {
        String charId = sub.split(":")[2];
        return Integer.parseInt(charId);
    }

    private User convertOAuthTokenToUser(String eveState, OAuthToken oAuthToken, String remoteIp) {
        EveJwtContent jwtContent = decodeJwt(oAuthToken.getAccess_token());
        User user = new UserImpl();
        user.setCharacterId(extractCharacterIdFromSub(jwtContent.getSub()));
        user.setName(jwtContent.getName());
        user.setIpAddress(remoteIp);
        user.setEveState(eveState);
        user.setRefreshToken(oAuthToken.getRefresh_token());
        user.setLastRefresh(LocalDateTime.now());
        user.setExpiresIn(oAuthToken.getExpires_in());
        return user;
    }


    private EveJwtContent decodeJwt(String jwtToken) {
        try {
            JWKSet publicKeys = JWKSet.load(new URL("https://login.eveonline.com/oauth/jwks "));
            RSAKey rsaPublicJWK = (RSAKey) publicKeys.getKeyByKeyId("JWT-Signature-Key").toPublicJWK();
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);
            if (!signedJWT.verify(verifier)) {
                throw new IOException("JWT verification failure");
            }
            return new ObjectMapper().readValue(signedJWT.getJWTClaimsSet().toJSONObject().toJSONString(), EveJwtContent.class);
        } catch (ParseException | IOException | JOSEException e) {
            throw new AccessDeniedException("Invalid JWT token");
        }
    }
}
