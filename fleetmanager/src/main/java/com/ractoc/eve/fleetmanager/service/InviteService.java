package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.Invite;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.InviteImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.InviteManager;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.MailApi;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailMail;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient;
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient.RecipientTypeEnum;
import com.ractoc.eve.user_client.api.UserResourceApi;
import com.ractoc.eve.user_client.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class InviteService {

    private final InviteManager inviteManager;
    private final UserResourceApi userResourceApi;
    private final MailApi mailApi;

    @Autowired
    public InviteService(InviteManager inviteManager, UserResourceApi userResourceApi, MailApi mailApi) {
        this.inviteManager = inviteManager;
        this.userResourceApi = userResourceApi;
        this.mailApi = mailApi;
    }

    public Stream<Invite> getInvitesForFleet(Integer fleetId) {
        return inviteManager.stream().filter(Invite.FLEET_ID.equal(fleetId));
    }

    public Stream<Invite> getInvitesForFleet(FleetModel fleet) {
        return inviteManager.stream().filter(Invite.FLEET_ID.equal(fleet.getId()));
    }

    public Invite getInvite(String key) {
        return inviteManager.stream().filter(Invite.KEY.equal(key)).findAny().orElseThrow(() -> new ServiceException("Invite not found for key " + key));
    }

    public Invite getInvite(Integer id) {
        return inviteManager.stream().filter(Invite.ID.equal(id)).findAny().orElseThrow(() -> new ServiceException("Invite not found for id " + id));
    }

    public Stream<Invite> getInvitesForCharacter(Integer characterId, Integer corpId) {
        return inviteManager.stream()
                .filter(Invite.TYPE.equal("character").and(Invite.INVITEE_ID.equal(characterId)))
                .filter(Invite.TYPE.equal("corporation").and(Invite.INVITEE_ID.equal(corpId)));
    }

    public Stream<Invite> getInvitesForFleetAndCharacter(Integer fleetId, Integer characterId, Integer corpId) {
        return inviteManager.stream()
                .filter(Invite.FLEET_ID.equal(fleetId))
                .filter(Invite.TYPE.equal("character").and(Invite.INVITEE_ID.equal(characterId)))
                .filter(Invite.TYPE.equal("corporation").and(Invite.INVITEE_ID.equal(corpId)));
    }

    public void deleteInvitation(Integer fleetId, int id) {
        Optional<Invite> invite = inviteManager.stream()
                .filter(Invite.FLEET_ID.equal(fleetId).and(Invite.ID.equal(id)))
                .findFirst();
        invite.ifPresent(inviteManager::remove);
    }

    public void deleteInvitation(Invite invite) {
        inviteManager.remove(invite);
    }

    // needs to be synchronized to make sure there are never any duplicate invite keys.
    public synchronized String invite(Integer fleetId, Integer id, String type) {
        String inviteKey = generateInviteKey();
        Invite invite = new InviteImpl();
        invite.setFleetId(fleetId);
        invite.setType(type);
        invite.setKey(inviteKey);
        invite.setInviteeId(id);
        inviteManager.persist(invite);
        return inviteKey;
    }

    private String generateInviteKey() {
        String inviteKey = UUID.randomUUID().toString();
        if (inviteManager.stream().anyMatch(Invite.KEY.equal(inviteKey))) {
            inviteKey = generateInviteKey();
        }
        return inviteKey;
    }

    public void sendInviteMail(Integer charId,
                               String charName,
                               Integer fleetId,
                               String fleetName,
                               Integer inviteeId,
                               String inviteeType,
                               String inviteeName,
                               String inviteKey,
                               String additionalInfo) throws ApiException, com.ractoc.eve.user_client.ApiException {
        PostCharactersCharacterIdMailMail mail = generateMail(charId,
                charName,
                fleetId,
                fleetName,
                inviteeId,
                inviteeName,
                RecipientTypeEnum.fromValue(inviteeType),
                additionalInfo,
                inviteKey);
        UserModel user = userResourceApi.getEveTools();
        mailApi.postCharactersCharacterIdMail(user.getCharId(), mail, null, user.getAccessToken());
    }

    private PostCharactersCharacterIdMailMail generateMail(Integer charId, String charName, Integer fleetId, String fleetName, Integer recipientId, String recipientName, RecipientTypeEnum recipientType, String additionalInfo, String inviteKey) {
        PostCharactersCharacterIdMailMail mail = new PostCharactersCharacterIdMailMail();
        PostCharactersCharacterIdMailRecipient recipientItem = new PostCharactersCharacterIdMailRecipient();
        recipientItem.setRecipientId(recipientId);
        recipientItem.setRecipientType(recipientType);
        mail.addRecipientsItem(recipientItem);
        mail.setSubject(String.format("Fleet event %s", fleetName));

        // TODO: link feet detail page as well
        String body = String.format("<font size=\"13\" color=\"#ff999999\">Hello %s,</font>" +
                        "<br><br>" +
                        "<font size=\"13\" color=\"#ff999999\">You have been invited to fleet event %s</font>" +
                        "<br><br>" +
                        "<font size=\"13\" color=\"#ff999999\">Please visit the following link to register for the event.</font>" +
                        "<br><br>" +
                        "%s" +
                        "<br><br>" +
                        "%s" +
                        "<font size=\"13\" color=\"#ff999999\">I hope to see you there.</font>" +
                        "<br><br>" +
                        "</font><font size=\"13\" color=\"#ffd98d00\"><a href=\"showinfo:1377//%s\">%s</a>",
                recipientName,
                generateFleetLink(fleetId, fleetName),
                fleetName,
                generateRegistrationLink(inviteKey),
                generateAdditionalInfoText(additionalInfo),
                charId,
                charName);
        mail.setBody(body);

        return mail;
    }

    private String generateRegistrationLink(String inviteKey) {
        return String.format("<font size=\"13\" color=\"#ffffe400\"><a href=\"http://31.21.178.162:8181/fleets/invite/%s\">Register</a></font>", inviteKey);
    }

    private String generateFleetLink(Integer fleetId, String fleetName) {
        return String.format("<font size=\"13\" color=\"#ffffe400\"><a href=\"http://31.21.178.162:8181/fleet/details/%s\">%s</a></font>", fleetId, fleetName);
    }

    private String generateAdditionalInfoText(String additionalInfo) {
        if (StringUtils.isNotBlank(additionalInfo)) {
            return additionalInfo + "<br><br>";
        } else {
            return "";
        }
    }
}
