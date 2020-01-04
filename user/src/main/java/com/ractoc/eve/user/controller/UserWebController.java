package com.ractoc.eve.user.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.ractoc.eve.user.model.AccessToken;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.UUID;

@Controller
public class UserWebController {
    @Value("${sso.client-url}")
    private String clientUrl;
    @Value("${sso.client-id}")
    private String clientId;
    @Value("${sso.client-secret}")
    private String clientSecret;
    @Value("${sso.client-scopes}")
    private String clientScopes;

    @GetMapping(value="/launchSignOn")
    public String launchSignOn() {
        return "redirect:https://login.eveonline.com/v2/oauth/authorize/"
                + "?response_type=code"
                + "&redirect_uri=" + clientUrl
                + "&client_id=" + clientId
                + "&scope=" + clientScopes
                + "&state=" + UUID.randomUUID().toString();
    }

    @GetMapping(value="/eveCallBack")
    public String eveCallBack(@RequestParam String code, @RequestParam String state) {
        // TODO: verify state
        MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        ContextResolver<MoxyJsonConfig> jsonConfigResolver = moxyJsonConfig.resolver();
        Feature basicAuth = HttpAuthenticationFeature.basic(clientId, clientSecret);
        Client client = ClientBuilder.newBuilder()
                .register(basicAuth)
                .register(jsonConfigResolver)
                .build();
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        AccessToken accessToken = client.target("https://login.eveonline.com/v2/oauth/token")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.form(formData), new GenericType<AccessToken>(){});

        // TODO: actually do something with the decoded JWT
        decodeJwt(accessToken.getAccess_token());

        String token = UUID.randomUUID().toString();
        return "redirect:http://localhost/?token=" + token;
    }

    // TODO: introduce proper error handling
    public void decodeJwt(String jwtToken) {
        try {
            JWKSet publicKeys = JWKSet.load(new URL("https://login.eveonline.com/oauth/jwks "));
            RSAKey rsaPublicJWK = (RSAKey) publicKeys.getKeyByKeyId("JWT-Signature-Key").toPublicJWK();
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);
            if (!signedJWT.verify(verifier)) {
                throw new IOException("JWT verification failure");
            }
        } catch (ParseException | IOException | JOSEException e) {
            e.printStackTrace();
        }
    }
}
