package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.MailApi;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailMail;
import com.ractoc.eve.user_client.api.UserResourceApi;
import com.ractoc.eve.user_client.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

    private final UserResourceApi userResourceApi;
    private final MailApi mailApi;

    @Autowired
    public MailUtil(UserResourceApi userResourceApi, MailApi mailApi) {
        this.userResourceApi = userResourceApi;
        this.mailApi = mailApi;
    }

    public void sendCharacterMail(PostCharactersCharacterIdMailMail mail) {
        for (int retryCount = 0; retryCount < 5; retryCount++) {
            try {
                UserModel eveTools = userResourceApi.getEveTools();
                mailApi.postCharactersCharacterIdMail(eveTools.getCharId(), mail, null, eveTools.getAccessToken());
                break;
            } catch (ApiException e) {
                System.out.println("ApiException Code: " + e.getCode());
                System.out.println("ApiException Message: " + e.getMessage());
                System.out.println("ApiException ResponseBody: " + e.getResponseBody());
                System.out.println("ApiException ResponseHeaders: " + e.getResponseHeaders());
                throw new ServiceException("unable to send invite mail", e);
            } catch (com.ractoc.eve.user_client.ApiException e) {
                e.printStackTrace();
            }
            System.out.println("retry " + retryCount + " for sending mail");
        }
    }
}
