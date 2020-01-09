package com.ractoc.eve.user.filter;

import com.ractoc.eve.user_client.ApiException;
import com.ractoc.eve.user_client.api.UserResourceApi;
import com.ractoc.eve.user_client.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class UserAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private UserResourceApi userResourceApi;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        if (Instant.ofEpochMilli(((EveUserDetails) userDetails).getExpiresAt()).isBefore(Instant.now())) {
            System.out.println("EVE-State has expired and needs to be refreshed");
            throw new DisabledException("EVE-State has expired and needs to be refreshed");
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authToken) throws AuthenticationException {
        Object token = authToken.getCredentials();
        return Optional.ofNullable(token)
                .map(String::valueOf)
                .map(this::getUserFromApi)
                .map(this::convertToEveUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with authentication token=" + token));
    }

    private UserModel getUserFromApi(String eveState) {
        try {
            return userResourceApi.getUser(eveState).getUser();
        } catch (ApiException e) {
            throw new UsernameNotFoundException("Cannot find user with authentication eveState=" + eveState);
        }
    }

    private EveUserDetails convertToEveUserDetails(UserModel user) {
        return new EveUserDetails(user.getCharacterName(), user.getEveState(),
                AuthorityUtils.createAuthorityList("USER"), user.getCharId(), user.getExpiresAt(), user.getIpAddress());
    }
}
