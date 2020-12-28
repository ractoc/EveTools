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

import java.util.Optional;
import java.util.stream.Stream;

import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.generated.GeneratedRegistrations.*;
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

    public Stream<Registrations> getRegistrationsForFleet(Integer fleetId) {
        return registrationsManager.stream().filter(FLEET_ID.equal(fleetId).and(ACCEPT.equal(true)));
    }

    public Registrations getRegistrationsForFleetForCharacter(Integer fleetId, Integer characterId) {
        return registrationsManager.stream()
                .filter(FLEET_ID.equal(fleetId)
                        .and(CHARACTER_ID.equal(characterId)
                                .and(ACCEPT.equal(true))))
                .findFirst()
                .orElseThrow(() -> new NoSuchEntryException("Registration not found"));
    }

    public Optional<Registrations> getRegistration(Integer fleetId, Integer charId) {
        return registrationsManager.stream().filter(FLEET_ID.equal(fleetId)).filter(CHARACTER_ID.equal(charId)).findFirst();
    }

    public Registrations registerForFleet(Integer fleetId, int charId, String charName, boolean accept) {
        Registrations registrations = new RegistrationsImpl();
        registrations.setCharacterId(charId);
        registrations.setFleetId(fleetId);
        registrations.setName(charName);
        registrations.setAccept(accept);
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

    public Registrations updateRegistration(Registrations registration) {
        registration.setAccept(
                getRegistration(registration.getFleetId(), registration.getCharacterId())
                        .orElseThrow(() -> new NoSuchEntryException("No existing registration to update"))
                        .getAccept());
        return registrationsManager.update(registration);
    }

    public void deleteRegistration(int fleetId, int charId) {
        Registrations registration = getRegistration(fleetId, charId).orElseThrow(() -> new NoSuchEntryException("Registration not found"));
        registration.setAccept(false);
        registrationsManager.update(registration);
    }

    public void deleteRegistration(Registrations registration) {
        registrationsManager.remove(registration);
    }
}
