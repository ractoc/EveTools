package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.MailApi;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailMail;
import com.ractoc.eve.user_client.api.UserResourceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

    private final MailApi mailApi;

    @Autowired
    public MailUtil(MailApi mailApi) {
        this.mailApi = mailApi;
    }

    public void sendCharacterMail(Integer charId, String token, PostCharactersCharacterIdMailMail mail) {
        try {
            mailApi.postCharactersCharacterIdMail(charId, mail, null, token);
        } catch (ApiException e) {
            System.out.println("ApiException Code: " + e.getCode());
            System.out.println("ApiException Message: " + e.getMessage());
            System.out.println("ApiException ResponseBody: " + e.getResponseBody());
            System.out.println("ApiException ResponseHeaders: " + e.getResponseHeaders());
            throw new ServiceException("unable to send invite mail", e);
        }
    }
}
