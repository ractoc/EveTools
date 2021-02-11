package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.Invite;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.InviteImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.InviteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class InviteService {

    private final InviteManager inviteManager;

    @Autowired
    public InviteService(InviteManager inviteManager) {
        this.inviteManager = inviteManager;
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
}
