package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.generated.GeneratedFleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.Invites;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.InvitesImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.InvitesManager;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.generated.GeneratedInvites;
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

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.InvitesManager.IDENTIFIER;
import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.generated.GeneratedInvites.FLEET_ID;
import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.generated.GeneratedInvites.KEY;

@Service
public class InviteService {

    private final InvitesManager invitesManager;
    private final JoinComponent joinComponent;

    private final MailApi mailApi;

    @Autowired
    public InviteService(InvitesManager invitesManager, JoinComponent joinComponent, MailApi mailApi) {
        this.invitesManager = invitesManager;
        this.joinComponent = joinComponent;
        this.mailApi = mailApi;
    }

    public Stream<Invites> getInvitesForFleet(Integer fleetId) {
        return invitesManager.stream().filter(FLEET_ID.equal(fleetId));
    }

    public Stream<Invites> getInvitesForFleet(FleetModel fleet) {
        return invitesManager.stream().filter(FLEET_ID.equal(fleet.getId()));
    }

    public Invites getInvite(String key) {
        return invitesManager.stream().filter(KEY.equal(key)).findAny().orElseThrow(() -> new ServiceException("Invite not found for jey " + key));
    }

    public Stream<Tuple2<Invites, Fleet>> getInvitesForCharacter(Integer characterId, Integer corpId) {
        Join<Tuple2<Invites, Fleet>> join = joinComponent.from(IDENTIFIER)
                .where(GeneratedInvites.ID.equal(characterId).or(GeneratedInvites.ID.equal(corpId)))
                .innerJoinOn(GeneratedFleet.ID).equal(FLEET_ID)
                .build(Tuples::of);
        return join.stream();
    }

    public void deleteInvitation(Integer fleetId, int id) {
        Optional<Invites> invite = invitesManager.stream()
                .filter(FLEET_ID.equal(fleetId).and(GeneratedInvites.ID.equal(id)))
                .findFirst();
        invite.ifPresent(invitesManager::remove);
    }

    public void deleteInvitation(Invites invite) {
        invitesManager.remove(invite);
    }

    // needs to be synchronized to make sure there are never any duplicate invite keys.
    public synchronized String invite(Integer fleetId, Integer id, String type, String name, String additionalInfo) {
        String inviteKey = generateInviteKey();
        Invites invite = new InvitesImpl();
        invite.setFleetId(fleetId);
        invite.setId(id);
        invite.setType(type);
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

    public void sendInviteMail(Integer charId,
                               String charName,
                               String fleetName,
                               Integer id,
                               String type,
                               String name,
                               String inviteKey,
                               String additionalInfo,
                               String accessToken) throws ApiException {
        PostCharactersCharacterIdMailMail mail = generateMail(charName,
                fleetName,
                id,
                name,
                RecipientTypeEnum.fromValue(type.toLowerCase(Locale.ROOT)),
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
