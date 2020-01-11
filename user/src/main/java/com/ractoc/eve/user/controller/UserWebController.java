package com.ractoc.eve.user.controller;

import com.ractoc.eve.user.handler.UserHandler;
import com.ractoc.eve.user.model.AccessDeniedException;
import com.ractoc.eve.user.model.OAuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

@Controller
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

    @Autowired
    public UserWebController(UserHandler handler, Client client) {
        this.handler = handler;
        this.client = client;
    }

    @GetMapping(value = "/launchSignOn")
    public String launchSignOn(HttpServletRequest request, @CookieValue(value = "eve-state", defaultValue = "") String eveState) {
        if (eveState.isEmpty()) {
            return initiateLogin(request);
        }
        return refreshToken(request, eveState);
    }

    @GetMapping(value = "/eveCallBack")
    public String eveCallBack(HttpServletRequest request, HttpServletResponse response, @RequestParam String code, @RequestParam(name = "state") String eveState) {
        validatedIP(eveState, RequestUtils.getRemoteIP(request));

        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        OAuthToken accessToken = client.target("https://login.eveonline.com/v2/oauth/token")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.form(formData), new GenericType<OAuthToken>() {
                });

        handler.storeEveUserRegistration(eveState, accessToken, RequestUtils.getRemoteIP(request));

        response.addCookie(new Cookie("eve-state", eveState));

        return REDIRECT + frontendUrl + "/" + eveState;
    }

    private String initiateLogin(HttpServletRequest request) {
        String eveState = handler.initiateLogin(RequestUtils.getRemoteIP(request));
        return "redirect:https://login.eveonline.com/v2/oauth/authorize/"
                + "?response_type=code"
                + "&redirect_uri=" + clientUrl
                + "&client_id=" + clientId
                + "&scope=" + clientScopes
                + "&state=" + eveState;
    }

    private String refreshToken(HttpServletRequest request, String eveState) {
        String refreshToken = handler.getRefreshTokenForState(eveState);
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        OAuthToken oAuthToken = client.target("https://login.eveonline.com/v2/oauth/token")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.form(formData), new GenericType<OAuthToken>() {
                });

        handler.storeEveUserRegistration(eveState, oAuthToken, RequestUtils.getRemoteIP(request));

        return REDIRECT + frontendUrl + "/" + eveState;
    }

    private void validatedIP(String eveState, String remoteIP) {
        if (!handler.getValidIpByState(eveState).equals(remoteIP)) {
            throw new AccessDeniedException("Unvalidated IP-Address");
        }
    }

}
