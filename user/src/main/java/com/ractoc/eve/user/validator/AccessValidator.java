package com.ractoc.eve.user.validator;

import com.ractoc.eve.user.handler.UserHandler;
import com.ractoc.eve.user.model.EveUserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
public class AccessValidator {

    @Autowired
    private UserHandler handler;

    public void validatedIP(String eveState, String remoteIP) throws AccessDeniedException {
        EveUserRegistration userRegistration = handler.getEveUserRegistration(eveState);
        if (!userRegistration.getIpAddress().equals(remoteIP)) {
            throw new AccessDeniedException("Unvalidated IP-Address");
        }
    }

    public void currentState(String eveState) throws AccessDeniedException {
        if (handler.getEveUserRegistration(eveState) == null) {
            throw new AccessDeniedException("Unknown state");
        }
    }
}
