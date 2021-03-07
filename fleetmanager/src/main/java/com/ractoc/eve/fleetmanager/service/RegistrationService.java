package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.Registrations;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.RegistrationsImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.RegistrationsManager;
import com.ractoc.eve.jesi.api.MailApi;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailMail;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient;
import com.ractoc.eve.user_client.api.UserResourceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.generated.GeneratedRegistrations.*;
import static com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient.RecipientTypeEnum.CHARACTER;

@Service
public class RegistrationService {

    private final RegistrationsManager registrationsManager;
    private final MailUtil mailUtil;

    @Autowired
    public RegistrationService(RegistrationsManager registrationsManager, MailUtil mailUtil) {
        this.registrationsManager = registrationsManager;
        this.mailUtil = mailUtil;
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


    public void registerForFleet(Integer fleetId, int charId, String charName) {
        // First we remove any existing fleet registration for this character on the fleet event.
        // This allows a character to re-register after first denying.
        getRegistration(fleetId, charId).ifPresent(this::deleteRegistration);

        Registrations registrations = new RegistrationsImpl();
        registrations.setCharacterId(charId);
        registrations.setFleetId(fleetId);
        registrations.setName(charName);
        registrations.setAccept(true);
        registrationsManager.persist(registrations);
    }

    public void unRegisterForFleet(Integer fleetId, int charId, String charName) {
        // First we remove any existing fleet registration for this character on the fleet event.
        // This allows a character to re-register after first denying.
        getRegistration(fleetId, charId).ifPresent(this::deleteRegistration);

        Registrations registrations = new RegistrationsImpl();
        registrations.setCharacterId(charId);
        registrations.setFleetId(fleetId);
        registrations.setName(charName);
        registrations.setAccept(false);
        registrationsManager.persist(registrations);
    }

    public void sendRegistrationNotification(Integer fleetId, String fleetName, int charId, String charName, String token, Integer ownerId, String ownerName) {
        PostCharactersCharacterIdMailMail mail = generateAcceptMail(charId, charName, fleetId, fleetName, ownerId, ownerName);
        mailUtil.sendCharacterMail(charId, token, mail);
    }

    private PostCharactersCharacterIdMailMail generateAcceptMail(int charId, String charName, Integer fleetId, String fleetName, Integer ownerId, String ownerName) {
        PostCharactersCharacterIdMailMail mail = new PostCharactersCharacterIdMailMail();
        PostCharactersCharacterIdMailRecipient recipientItem = new PostCharactersCharacterIdMailRecipient();
        recipientItem.setRecipientId(ownerId);
        recipientItem.setRecipientType(CHARACTER);
        mail.addRecipientsItem(recipientItem);
        String body = String.format("<font size=\"13\" color=\"#ff999999\">Hello %s,</font>" +
                        "<br><br>" +
                        "<font size=\"13\" color=\"#ff999999\">I accepted your invitation for the fleet event, %s.</font>" +
                        "<br><br>" +
                        "<font size=\"13\" color=\"#ff999999\">I will see you there.</font>" +
                        "<br><br>" +
                        "</font><font size=\"13\" color=\"#ffd98d00\"><a href=\"showinfo:1377//%s\">%s</a>",
                ownerName,
                generateFleetLink(fleetId, fleetName),
                charId,
                charName);
        mail.setBody(body);
        mail.setSubject(String.format("Fleet event %s", fleetName));
        return mail;
    }

    private String generateFleetLink(Integer fleetId, String fleetName) {
        return String.format("<font size=\"13\" color=\"#ffffe400\"><a href=\"http://31.21.178.162:8181/fleet/details/%s\">%s</a></font>", fleetId, fleetName);
    }

    public void sendDenyNotification(Integer fleetId, String fleetName, int charId, String charName, String token, Integer ownerId, String ownerName) {
        PostCharactersCharacterIdMailMail mail = generateDenyMail(charId, charName, fleetId, fleetName, ownerId, ownerName);
        mailUtil.sendCharacterMail(charId, token, mail);
    }

    private PostCharactersCharacterIdMailMail generateDenyMail(int charId, String charName, Integer fleetId, String fleetName, Integer ownerId, String ownerName) {
        PostCharactersCharacterIdMailMail mail = new PostCharactersCharacterIdMailMail();
        PostCharactersCharacterIdMailRecipient recipientItem = new PostCharactersCharacterIdMailRecipient();
        recipientItem.setRecipientId(ownerId);
        recipientItem.setRecipientType(CHARACTER);
        mail.addRecipientsItem(recipientItem);
        String body = String.format("<font size=\"13\" color=\"#ff999999\">Hello %s,</font>" +
                        "<br><br>" +
                        "<font size=\"13\" color=\"#ff999999\">I have not accepted your invitation for the fleet event, %s.</font>" +
                        "<br><br>" +
                        "<font size=\"13\" color=\"#ff999999\">I will not see you there.</font>" +
                        "<br><br>" +
                        "</font><font size=\"13\" color=\"#ffd98d00\"><a href=\"showinfo:1377//%s\">%s</a>",
                ownerName,
                generateFleetLink(fleetId, fleetName),
                charId,
                charName);
        mail.setBody(body);
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
