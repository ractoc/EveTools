package com.ractoc.eve.user.controller;

import com.ractoc.eve.user.handler.UserHandler;
import com.ractoc.eve.user.model.AccessDeniedException;
import com.ractoc.eve.user.model.OAuthToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

@Controller
@Log4j2
public class UserWebController {

    public static final String REDIRECT = "redirect:";

    private final UserHandler handler;
    private final Client client;

    @Value("${sso.frontend-url}")
    private String frontendUrl;
    @Value("${sso.client-url}")
    private String clientUrl;
    @Value("${sso.client-id}")
    private String clientId;
    @Value("${sso.client-scopes}")
    private String clientScopes;
    @Value("${sso.evetools-scopes}")
    private String evetoolsScopes;

    @Autowired
    public UserWebController(UserHandler handler, Client client) {
        this.handler = handler;
        this.client = client;
    }

    @GetMapping(value = "/launchSignOn")
    public String launchSignOn(@CookieValue(value = "eve-state", defaultValue = "") String eveState) {
        log.trace("launching signon for state " + eveState);
        if (eveState.isEmpty()) {
            return initiateLogin(clientScopes);
        }
        return refreshToken(eveState);
    }

    @GetMapping(value = "/evetools")
    public String launchEveToolsSignOn() {
        log.trace("getting user details for evetools");
        return initiateLogin(evetoolsScopes);
    }

    @GetMapping(value = "/eveCallBack")
    public String eveCallBack(@RequestParam String code, @RequestParam(name = "state") String eveState) {
        log.trace("eveCallback for code "+ code + " and state " + eveState);
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        OAuthToken accessToken = client.target("https://login.eveonline.com/v2/oauth/token")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.form(formData), new GenericType<>() {
                });

        handler.storeEveUserRegistration(eveState, accessToken);

        return REDIRECT + frontendUrl + "/" + eveState;
    }

    private String initiateLogin(String scopes) {
        String eveState = handler.initiateLogin();
        log.trace("initiate login for state " + eveState + " and scopes " + scopes);
        return "redirect:https://login.eveonline.com/v2/oauth/authorize/"
                + "?response_type=code"
                + "&redirect_uri=" + clientUrl
                + "&client_id=" + clientId
                + "&scope=" + scopes
                + "&state=" + eveState;
    }

    private String refreshToken(String eveState) {
        log.trace("refreshing token for state " + eveState);
        try {
            String refreshToken = handler.getRefreshTokenForState(eveState);
            MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);
            OAuthToken oAuthToken = client.target("https://login.eveonline.com/v2/oauth/token")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.form(formData), new GenericType<>() {
                    });

            handler.storeEveUserRegistration(eveState, oAuthToken);

            return REDIRECT + frontendUrl + "/" + eveState;
        } catch (AccessDeniedException ade) {
            return initiateLogin(clientScopes);
        }
    }

}
