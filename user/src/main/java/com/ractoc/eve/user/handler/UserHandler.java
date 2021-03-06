package com.ractoc.eve.user.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.ractoc.eve.domain.user.UserModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.user.db.user.eve_user.user.User;
import com.ractoc.eve.user.db.user.eve_user.user.UserImpl;
import com.ractoc.eve.user.model.AccessDeniedException;
import com.ractoc.eve.user.model.EveJwtContent;
import com.ractoc.eve.user.model.OAuthToken;
import com.ractoc.eve.user.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Log4j2
public class UserHandler {

    private final UserService service;

    @Value("${sso.evetools-charid}")
    private Integer evetoolsCharId;

    @Autowired
    public UserHandler(UserService service) {
        this.service = service;
    }

    public String initiateLogin() {
        log.trace("initiate initial login");
        return service.initializeUser();
    }

    public String getRefreshTokenForState(String eveState) {
        log.trace("refresh token for state " + eveState);
        return getUser(eveState)
                .map(User::getRefreshToken)
                .orElseThrow(() -> new AccessDeniedException(eveState))
                .orElseThrow(() -> new AccessDeniedException(eveState));
    }

    public void storeEveUserRegistration(String eveState, OAuthToken oAuthToken) {
        log.trace("store user for state " + eveState);
        service.updateUser(convertOAuthTokenToUser(eveState, oAuthToken));
    }

    public UserModel getUserByState(String eveState) {
        log.trace("get user for state " + eveState);
        return getUser(eveState)
                .map(user -> {
                    UserModel result = UserModel.builder()
                            .charId(user.getCharacterId().orElseThrow(() -> new AccessDeniedException(eveState)))
                            .characterName(user.getName().orElseThrow(() -> new AccessDeniedException(eveState)))
                            .eveState(eveState)
                            .expiresAt(user.getLastRefresh().orElseThrow(() -> new AccessDeniedException(eveState))
                                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000L *
                                    user.getExpiresIn().orElseThrow(() -> new AccessDeniedException(eveState))))
                            .accessToken(user.getAccessToken().orElseThrow(() -> new AccessDeniedException(eveState)))
                            .build();
                    result.setRoles(getRoles(result.getCharId(), result.getAccessToken(), eveState));
                    return result;
                })
                .orElseThrow(() -> new AccessDeniedException(eveState));
    }

    private List<String> getRoles(int characterId, String accessToken, String eveState) {
        try {
            return service.getRoles(characterId, accessToken).stream().map(Enum::name).collect(Collectors.toList());
        } catch (ApiException e) {
            throw new AccessDeniedException(eveState, e);
        }
    }

    public UserModel getUserNameByState(String eveState) {
        log.trace("get user name for state " + eveState);
        return getUser(eveState)
                .map(user ->
                        UserModel.builder()
                                .characterName(
                                        user.getName().orElseThrow(() -> new AccessDeniedException(eveState)))
                                .roles(getRoles(
                                        user.getCharacterId().orElseThrow(() -> new AccessDeniedException(eveState)),
                                        user.getAccessToken().orElseThrow(() -> new AccessDeniedException(eveState)),
                                        eveState))
                                .eveState(eveState)
                                .build())
                .orElseThrow(() -> new AccessDeniedException(eveState));
    }

    public void logoutUser(String eveState) {
        log.trace("logout user for state " + eveState);
        service.logoutUser(eveState);
    }

    private Optional<User> getUser(String eveState) {
        log.trace("get user for state " + eveState);
        return service.getUser(eveState);
    }

    public UserModel getEvetools() {
        log.trace("get evetools");
        return service.getUser(evetoolsCharId)
                .map(user -> {
                    UserModel result = UserModel.builder()
                            .charId(user.getCharacterId().orElseThrow(() -> new AccessDeniedException("evetools")))
                            .characterName(user.getName().orElseThrow(() -> new AccessDeniedException("evetools")))
                            .eveState(user.getEveState())
                            .expiresAt(user.getLastRefresh().orElseThrow(() -> new AccessDeniedException("evetools"))
                                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000L *
                                    user.getExpiresIn().orElseThrow(() -> new AccessDeniedException("evetools"))))
                            .accessToken(user.getAccessToken().orElseThrow(() -> new AccessDeniedException("evetools")))
                            .build();
                    return result;
                })
                .orElseThrow(() -> new AccessDeniedException("evetools"));
    }

    private int extractCharacterIdFromSub(String sub) {
        String charId = sub.split(":")[2];
        return Integer.parseInt(charId);
    }

    private User convertOAuthTokenToUser(String eveState, OAuthToken oAuthToken) {
        EveJwtContent jwtContent = decodeJwt(oAuthToken.getAccess_token());
        User user = new UserImpl();
        user.setCharacterId(extractCharacterIdFromSub(jwtContent.getSub()));
        user.setName(jwtContent.getName());
        user.setEveState(eveState);
        user.setRefreshToken(oAuthToken.getRefresh_token());
        user.setLastRefresh(LocalDateTime.now());
        user.setExpiresIn(oAuthToken.getExpires_in());
        user.setAccessToken(oAuthToken.getAccess_token());
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
            throw new AccessDeniedException("Invalid JWT token", e);
        }
    }
}
