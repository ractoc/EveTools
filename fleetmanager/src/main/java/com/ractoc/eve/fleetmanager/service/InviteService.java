package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.Invite;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.InviteImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.InviteManager;
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

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class InviteService {

    private final InviteManager inviteManager;
    private final JoinComponent joinComponent;

    private final MailApi mailApi;

    @Autowired
    public InviteService(InviteManager inviteManager, JoinComponent joinComponent, MailApi mailApi) {
        this.inviteManager = inviteManager;
        this.joinComponent = joinComponent;
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

    public Optional<Invite> getInvite(Integer fleetId, Integer charId, Integer corpId) {
        return inviteManager.stream()
                .filter(Invite.FLEET_ID.equal(fleetId))
                .filter(Invite.CHAR_ID.equal(charId).or(Invite.CORP_ID.equal(corpId)))
                .findAny();
    }

    public Stream<Tuple2<Invite, Fleet>> getInvitesForCharacter(Integer characterId, Integer corpId) {
        Join<Tuple2<Invite, Fleet>> join = joinComponent.from(InviteManager.IDENTIFIER)
                .where(Invite.CHAR_ID.equal(characterId).or(Invite.CORP_ID.equal(corpId)))
                .innerJoinOn(Fleet.ID).equal(Invite.FLEET_ID)
                .build(Tuples::of);
        return join.stream();
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
    public synchronized String invite(Integer fleetId, Integer charId, Integer corpId, String name) {
        String inviteKey = generateInviteKey();
        Invite invite = new InviteImpl();
        invite.setFleetId(fleetId);
        invite.setName(name);
        invite.setKey(inviteKey);
        invite.setCharId(charId);
        invite.setCorpId(corpId);
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
                               String fleetName,
                               Integer id,
                               String name,
                               RecipientTypeEnum type,
                               String inviteKey,
                               String additionalInfo,
                               String accessToken) throws ApiException {
        PostCharactersCharacterIdMailMail mail = generateMail(charName,
                fleetName,
                id,
                name,
                type,
                additionalInfo,
                inviteKey);
        mailApi.postCharactersCharacterIdMail(charId, mail, null, accessToken);
    }

    private PostCharactersCharacterIdMailMail generateMail(String charName, String fleetName, Integer recipientId, String recipientName, RecipientTypeEnum recipientType, String additionalInfo, String inviteKey) {
        PostCharactersCharacterIdMailMail mail = new PostCharactersCharacterIdMailMail();
        PostCharactersCharacterIdMailRecipient recipientItem = new PostCharactersCharacterIdMailRecipient();
        recipientItem.setRecipientId(recipientId);
        recipientItem.setRecipientType(recipientType);
        mail.addRecipientsItem(recipientItem);
        mail.setBody(String.format("Hello %s,%n%n" +
                        "You have been invited to a fleet event, %s.%n" +
                        "Please visit the following link to register for the event.%n%n" +
                        "%s%n%n" +
                        "%s" +
                        "I hope to see you there.%n%n" +
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
            return additionalInfo + "%n%n";
        } else {
            return "";
        }
    }
}
