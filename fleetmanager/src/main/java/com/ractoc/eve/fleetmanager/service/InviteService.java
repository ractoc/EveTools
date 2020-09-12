package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.Invites;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.InvitesImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.InvitesManager;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.MailApi;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailMail;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient.RecipientTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.generated.GeneratedInvites.INVITE_KEY;

@Service
public class InviteService {

    private final InvitesManager invitesManager;
    private final MailApi mailApi;

    @Autowired
    public InviteService(InvitesManager invitesManager, MailApi mailApi) {
        this.invitesManager = invitesManager;
        this.mailApi = mailApi;
    }

    public String inviteCorporation(Integer fleetId, Integer corporationId, String name) {
        return invite(fleetId, corporationId, null, name);
    }

    public String inviteCharacter(Integer fleetId, Integer characterId, String name) {
        return invite(fleetId, null, characterId, name);
    }

    public void sendInviteCorporationMail(Integer characterId, String charName, String fleetName, Integer recipientId, String recipientName, String inviteKey, String accessToken) {
        try {
            sendInviteMail(characterId, charName, fleetName, recipientId, recipientName, RecipientTypeEnum.CORPORATION, inviteKey, accessToken);
        } catch (ApiException e) {
            throw new ServiceException("unable to send invite mail", e);
        }
    }

    public void sendInviteCharacterMail(Integer characterId, String charName, String fleetName, Integer recipientId, String recipientName, String inviteKey, String accessToken) {
        try {
            sendInviteMail(characterId, charName, fleetName, recipientId, recipientName, RecipientTypeEnum.CHARACTER, inviteKey, accessToken);
        } catch (ApiException e) {
            throw new ServiceException("unable to send invite mail", e);
        }
    }

    // needs to be synchronized to make sure there are never any duplicate invite keys.
    private synchronized String invite(Integer fleetId, Integer corporationId, Integer characterId, String name) {
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
        invite.setInviteKey(inviteKey);
        invitesManager.persist(invite);
        return inviteKey;
    }

    private String generateInviteKey() {
        String inviteKey = UUID.randomUUID().toString();
        if (invitesManager.stream().anyMatch(INVITE_KEY.equal(inviteKey))) {
            inviteKey = generateInviteKey();
        }
        return inviteKey;
    }

    private void sendInviteMail(Integer characterId, String charName, String fleetName, Integer recipientId, String recipientName, RecipientTypeEnum recipientType, String inviteKey, String accessToken) throws ApiException {
        PostCharactersCharacterIdMailMail mail = generateMail(charName, fleetName, recipientId, recipientName, recipientType, inviteKey);
        mailApi.postCharactersCharacterIdMail(characterId, mail, null, accessToken);
    }

    private PostCharactersCharacterIdMailMail generateMail(String charName, String fleetName, Integer recipientId, String recipientName, RecipientTypeEnum recipientType, String inviteKey) {
        PostCharactersCharacterIdMailMail mail = new PostCharactersCharacterIdMailMail();
        PostCharactersCharacterIdMailRecipient recipientItem = new PostCharactersCharacterIdMailRecipient();
        recipientItem.setRecipientId(recipientId);
        recipientItem.setRecipientType(recipientType);
        mail.addRecipientsItem(recipientItem);
        mail.setBody(String.format("Hello %s,\n\n" +
                        "You have been invited to a fleet event, %s.\n" +
                        "Please visit the following link to register for the event.\n\n" +
                        "%s\n\n" +
                        "I hope to see you there.\n\n" +
                        "%s",
                recipientName,
                fleetName,
                generateLink(inviteKey),
                charName));
        mail.setSubject(String.format("Fleet event %s", fleetName));
        return mail;
    }

    private String generateLink(String inviteKey) {
        return String.format("http://31.21.178.162:8181/fleets/register/%s", inviteKey);
    }
}
