package com.ractoc.eve.user.filter;

import com.ractoc.eve.user_client.ApiException;
import com.ractoc.eve.user_client.api.UserResourceApi;
import com.ractoc.eve.user_client.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class UserAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private UserResourceApi userResourceApi;

    @Autowired
    public UserAuthenticationProvider(UserResourceApi userResourceApi) {
        this.userResourceApi = userResourceApi;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        if (Instant.ofEpochMilli(((EveUserDetails) userDetails).getExpiresAt()).isBefore(Instant.now())) {
            throw new CredentialsExpiredException("EVE-State has expired and needs to be refreshed");
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authToken) {
        Object token = authToken.getCredentials();
        return Optional.ofNullable(token)
                .map(String::valueOf)
                .map(this::getUserFromApi)
                .map(this::convertToEveUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with authentication token=" + token));
    }

    private UserModel getUserFromApi(String eveState) {
        int retryCount = 0;
        while (retryCount < 10) {
            try {
                return userResourceApi.getUserDetails(eveState);
            } catch (ApiException e) {
                if (e.getCode() != 5020) {
                    throw new UsernameNotFoundException("Cannot find user with authentication eveState=" + eveState);
                }
                retryCount++;
            }
        }
        throw new UsernameNotFoundException("Cannot find user with authentication eveState=" + eveState);
    }

    private EveUserDetails convertToEveUserDetails(UserModel user) {
        return new EveUserDetails(user.getCharacterName(),
                user.getEveState(),
                AuthorityUtils.createAuthorityList(user.getRoles().toArray(new String[]{})),
                user.getCharId(),
                user.getExpiresAt(),
                user.getAccessToken());
    }
}
