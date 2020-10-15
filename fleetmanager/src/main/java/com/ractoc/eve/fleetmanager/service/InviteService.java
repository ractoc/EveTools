package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.db.FleetmanagerApplication;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.Invites;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.InvitesImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.InvitesManager;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.MailApi;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailMail;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient.RecipientTypeEnum;
import com.speedment.common.tuple.Tuple2;
import com.speedment.common.tuple.Tuples;
import com.speedment.runtime.join.Join;
import com.speedment.runtime.join.JoinComponent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Stream;

import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.generated.GeneratedInvites.*;

@Service
public class InviteService {

    private final FleetmanagerApplication app;

    private final InvitesManager invitesManager;
    private final MailApi mailApi;

    @Autowired
    public InviteService(FleetmanagerApplication app, InvitesManager invitesManager, MailApi mailApi) {
        this.app = app;
        this.invitesManager = invitesManager;
        this.mailApi = mailApi;
    }

    public String inviteCorporation(Integer fleetId, Integer corporationId, String name, String additionalInfo) {
        return invite(fleetId, corporationId, null, name, additionalInfo);
    }

    public String inviteCharacter(Integer fleetId, Integer characterId, String name, String additionalInfo) {
        return invite(fleetId, null, characterId, name, additionalInfo);
    }

    public void sendInviteCorporationMail(Integer characterId, String charName, String fleetName, Integer recipientId, String recipientName, String inviteKey, String additionalInfo, String accessToken) {
        try {
            sendInviteMail(characterId, charName, fleetName, recipientId, recipientName, RecipientTypeEnum.CORPORATION, inviteKey, additionalInfo, accessToken);
        } catch (ApiException e) {
            throw new ServiceException("unable to send invite mail", e);
        }
    }

    public void sendInviteCharacterMail(Integer characterId, String charName, String fleetName, Integer recipientId, String recipientName, String inviteKey, String additionalInfo, String accessToken) {
        try {
            sendInviteMail(characterId, charName, fleetName, recipientId, recipientName, RecipientTypeEnum.CHARACTER, inviteKey, additionalInfo, accessToken);
        } catch (ApiException e) {
            throw new ServiceException("unable to send invite mail", e);
        }
    }

    public Stream<Invites> getInvitesForFleet(Integer fleetId, Integer charId) {
        return invitesManager.stream().filter(FLEET_ID.equal(fleetId));
    }

    public Stream<Invites> getInvitesForFleet(FleetModel fleet) {
        return invitesManager.stream().filter(FLEET_ID.equal(fleet.getId()));
    }

    public Invites getInvite(String key) {
        return invitesManager.stream().filter(KEY.equal(key)).findAny().orElseThrow(() -> new ServiceException("Invite not found for jey " + key));
    }

    public Stream<Tuple2<Invites, Fleet>> getInvitesForCharacter(Integer characterId, Integer corpId) {
        JoinComponent joinComponent = app.getOrThrow(JoinComponent.class);
        Join<Tuple2<Invites, Fleet>> join = joinComponent.from(invitesManager.IDENTIFIER)
                .where(CHARACTER_ID.equal(characterId).or(CORPORATION_ID.equal(corpId)))
                .innerJoinOn(Fleet.ID).equal(FLEET_ID)
                .build(Tuples::of);
        return join.stream();
    }

    public void deleteInvitation(Integer fleetId, int charId, int corpId) {
        Invites invite = invitesManager.stream()
                .filter(FLEET_ID.equal(fleetId).and(CHARACTER_ID.equal(charId).or(CORPORATION_ID.equal(corpId))))
                .findFirst()
                .orElseThrow(() -> new NoSuchEntryException(String.format("No invite found for fleet id %d and character id %d", fleetId, charId)));
        invitesManager.remove(invite);
    }

    public void deleteInvitation(Invites invite) {
        invitesManager.remove(invite);
    }

    // needs to be synchronized to make sure there are never any duplicate invite keys.
    private synchronized String invite(Integer fleetId, Integer corporationId, Integer characterId, String name, String additionalInfo) {
        String inviteKey = generateInviteKey();
        Invites invite = new InvitesImpl();
        invite.setFleetId(fleetId);
        if (corporationId != null) {
            invite.setCorporationId(corporationId);
        }
        if (characterId != null) {
            invite.setCharacterId(characterId);
        }
        invite.setName(name);
        invite.setKey(inviteKey);
        invite.setAdditionalInfo(additionalInfo);
        invitesManager.persist(invite);
        return inviteKey;
    }

    private String generateInviteKey() {
        String inviteKey = UUID.randomUUID().toString();
        if (invitesManager.stream().anyMatch(KEY.equal(inviteKey))) {
            inviteKey = generateInviteKey();
        }
        return inviteKey;
    }

    private void sendInviteMail(Integer characterId, String charName, String fleetName, Integer recipientId, String recipientName, RecipientTypeEnum recipientType, String inviteKey, String additionalInfo, String accessToken) throws ApiException {
        PostCharactersCharacterIdMailMail mail = generateMail(charName, fleetName, recipientId, recipientName, recipientType, additionalInfo, inviteKey);
        mailApi.postCharactersCharacterIdMail(characterId, mail, null, accessToken);
    }

    private PostCharactersCharacterIdMailMail generateMail(String charName, String fleetName, Integer recipientId, String recipientName, RecipientTypeEnum recipientType, String additionalInfo, String inviteKey) {
        PostCharactersCharacterIdMailMail mail = new PostCharactersCharacterIdMailMail();
        PostCharactersCharacterIdMailRecipient recipientItem = new PostCharactersCharacterIdMailRecipient();
        recipientItem.setRecipientId(recipientId);
        recipientItem.setRecipientType(recipientType);
        mail.addRecipientsItem(recipientItem);
        mail.setBody(String.format("Hello %s,\n\n" +
                        "You have been invited to a fleet event, %s.\n" +
                        "Please visit the following link to register for the event.\n\n" +
                        "%s\n\n" +
                        "%s" +
                        "I hope to see you there.\n\n" +
                        "%s",
                recipientName,
                fleetName,
                generateLink(inviteKey),
                generateAdditionalInfoText(additionalInfo),
                charName));
        mail.setSubject(String.format("Fleet event %s", fleetName));
        return mail;
    }

    private String generateLink(String inviteKey) {
        return String.format("http://31.21.178.162:8181/fleets/invite/%s", inviteKey);
    }

    private String generateAdditionalInfoText(String additionalInfo) {
        if (StringUtils.isNotBlank(additionalInfo)) {
            return String.format("%s\n\n", additionalInfo);
        } else {
            return "";
        }
    }
}
