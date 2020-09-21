package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.Registrations;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.RegistrationsImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.RegistrationsManager;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.MailApi;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailMail;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient.RecipientTypeEnum.CHARACTER;

@Service
public class RegistrationService {

    private final RegistrationsManager registrationsManager;
    private final MailApi mailApi;

    @Autowired
    public RegistrationService(RegistrationsManager registrationsManager, MailApi mailApi) {
        this.registrationsManager = registrationsManager;
        this.mailApi = mailApi;
    }

    public Registrations registerForFleet(Integer fleetId, int charId, String charName) {
        Registrations registrations = new RegistrationsImpl();
        registrations.setCharacterId(charId);
        registrations.setFleetId(fleetId);
        registrations.setName(charName);
        return registrationsManager.persist(registrations);
    }

    public void sendRegistrationNotification(String fleetName, int characterId, String charName, Integer ownerId, String ownerName, String accessToken) {
        try {
            PostCharactersCharacterIdMailMail mail = generateAcceptMail(charName, fleetName, ownerId, ownerName);
            mailApi.postCharactersCharacterIdMail(characterId, mail, null, accessToken);
        } catch (ApiException e) {
            throw new ServiceException("unable to send invite mail", e);
        }
    }

    private PostCharactersCharacterIdMailMail generateAcceptMail(String charName, String fleetName, Integer ownerId, String ownerName) {
        PostCharactersCharacterIdMailMail mail = new PostCharactersCharacterIdMailMail();
        PostCharactersCharacterIdMailRecipient recipientItem = new PostCharactersCharacterIdMailRecipient();
        recipientItem.setRecipientId(ownerId);
        recipientItem.setRecipientType(CHARACTER);
        mail.addRecipientsItem(recipientItem);
        mail.setBody(String.format("Hello %s,\n\n" +
                        "I accepted your invitation for the fleet event, %s.\n\n" +
                        "I will see you there.\n\n" +
                        "%s",
                ownerName,
                fleetName,
                charName));
        mail.setSubject(String.format("Fleet event %s", fleetName));
        return mail;
    }

    public void sendDenyNotification(String fleetName, int characterId, String charName, Integer ownerId, String ownerName, String accessToken) {
        try {
            PostCharactersCharacterIdMailMail mail = generateDenyMail(charName, fleetName, ownerId, ownerName);
            mailApi.postCharactersCharacterIdMail(characterId, mail, null, accessToken);
        } catch (ApiException e) {
            throw new ServiceException("unable to send invite mail", e);
        }
    }

    private PostCharactersCharacterIdMailMail generateDenyMail(String charName, String fleetName, Integer ownerId, String ownerName) {
        PostCharactersCharacterIdMailMail mail = new PostCharactersCharacterIdMailMail();
        PostCharactersCharacterIdMailRecipient recipientItem = new PostCharactersCharacterIdMailRecipient();
        recipientItem.setRecipientId(ownerId);
        recipientItem.setRecipientType(CHARACTER);
        mail.addRecipientsItem(recipientItem);
        mail.setBody(String.format("Hello %s,\n\n" +
                        "I have not accepted your invitation for the fleet event, %s.\n\n" +
                        "I will not see you there.\n\n" +
                        "%s",
                ownerName,
                fleetName,
                charName));
        mail.setSubject(String.format("Fleet event %s", fleetName));
        return mail;
    }
}
