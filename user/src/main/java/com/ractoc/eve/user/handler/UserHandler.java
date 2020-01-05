package com.ractoc.eve.user.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.ractoc.eve.user.model.EveJwtContent;
import com.ractoc.eve.user.model.EveUserRegistration;
import com.ractoc.eve.user.model.OAuthToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserHandler {

    // TODO: save in database via Service
    private EveUserRegistration userRegistration;
    public EveUserRegistration getEveUserRegistration(String eveState) {
        return userRegistration;
    }

    public String initiateLogin(String remoteIP) {
        System.out.println("request coming from: " + remoteIP);
        userRegistration = new EveUserRegistration();
        userRegistration.setEveState(UUID.randomUUID().toString());
        userRegistration.setIpAddress(remoteIP);
        // TODO: Save userRegistration
        return userRegistration.getEveState();
    }

    public String getRefreshTokenForState(String eveState) {
        return getEveUserRegistration(eveState).getRefreshToken();
    }

    public void storeEveUserRegistration(String eveState, OAuthToken oAuthToken, String remoteIp) throws AccessDeniedException {
        userRegistration = convertOAuthTokenToEveUserRegistration(eveState, oAuthToken, remoteIp);
        // TODO: Update userRegistration
    }

    private int extractCharacterIdFromSub(String sub) {
        String charId = sub.split(":")[2];
        System.out.println("charId: " + charId);
        return Integer.parseInt(charId);
    }

    private EveUserRegistration convertOAuthTokenToEveUserRegistration(String eveState, OAuthToken oAuthToken, String remoteIp) throws AccessDeniedException {
        EveJwtContent jwtContent = decodeJwt(oAuthToken.getAccess_token());
        EveUserRegistration userReg = new EveUserRegistration();
        userReg.setCharacterId(extractCharacterIdFromSub(jwtContent.getSub()));
        userReg.setName(jwtContent.getName());
        userReg.setIpAddress(remoteIp);
        userReg.setEveState(eveState);
        userReg.setRefreshToken(oAuthToken.getRefresh_token());
        userReg.setLastrefresh(LocalDateTime.now());
        userReg.setExpiresIn(oAuthToken.getExpires_in());
        System.out.println("userRegistration: " + userReg);
        return userReg;
    }


    private EveJwtContent decodeJwt(String jwtToken) throws AccessDeniedException {
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
            e.printStackTrace();
            throw new AccessDeniedException("Invalid JWT token");
        }
    }
}
