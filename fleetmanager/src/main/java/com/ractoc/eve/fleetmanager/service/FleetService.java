package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.domain.fleetmanager.TypeModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.FleetManager;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_fleet.RoleFleetManager;
import com.ractoc.eve.fleetmanager.model.FleetSearchParams;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.generated.GeneratedFleet.OWNER;
import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.generated.GeneratedFleet.TYPE_ID;

@Service
public class FleetService {

    private final FleetManager fleetManager;
    private final RoleFleetManager roleFleetManager;
    private final RegistrationService registrationService;
    private final InviteService inviteService;

    @Autowired
    public FleetService(FleetManager fleetManager,
                        RoleFleetManager roleFleetManager,
                        RegistrationService registrationService,
                        InviteService inviteService) {
        this.fleetManager = fleetManager;
        this.roleFleetManager = roleFleetManager;
        this.registrationService = registrationService;
        this.inviteService = inviteService;
    }

    public Stream<Fleet> searchFleets(FleetSearchParams params, int charId, int corpId) {
        Stream<Fleet> fleets = fleetManager.stream();
        if (params.getStart() != null) {
            fleets = fleets.filter(Fleet.START_DATE_TIME.greaterThan(Timestamp.valueOf(params.getStart())));
        }
        if (params.getEnd() != null) {
            fleets = fleets.filter(Fleet.START_DATE_TIME.lessThan(Timestamp.valueOf(params.getEnd())));
        }
        if (params.isOwned()) {
            fleets.filter(OWNER.equal(charId));
        }
        if (ArrayUtils.isNotEmpty(params.getFleetTypes())) {
            fleets = fleets.filter(TYPE_ID.in(Arrays.stream(params.getFleetTypes()).map(TypeModel::getId).collect(Collectors.toList())));
        }
        return fleets;
    }

    private Stream<Fleet> searchFleetsInvited(FleetSearchParams params, Integer charId, Integer corpId) {
        JoinBuilder1<Fleet> join = joinComponent.from(FleetManager.IDENTIFIER);
        if (params.getStart() != null) {
            join = join.where(Fleet.START_DATE_TIME.greaterThan(Timestamp.valueOf(params.getStart())));
        }
        if (params.getEnd() != null) {
            join = join.where(Fleet.START_DATE_TIME.lessThan(Timestamp.valueOf(params.getEnd())));
        }
        if (ArrayUtils.isNotEmpty(params.getFleetTypes())) {
            join = join.where(TYPE_ID.in(Arrays.stream(params.getFleetTypes()).map(TypeModel::getId).collect(Collectors.toList())));
        }
        return join.innerJoinOn(Invite.FLEET_ID).equal(Fleet.ID)
                .where((Invite.TYPE.equal("character").and(Invite.INVITEE_ID.equal(charId)))
                        .or((Invite.TYPE.equal("corporation").and(Invite.INVITEE_ID.equal(corpId)))))
                .build()
                .stream()
                .map(Tuple2OfNullables::get0)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Optional<Fleet> getFleet(int id) {
        return fleetManager.stream().filter(fleet -> fleet.getId() == id).findFirst();
    }

    public Fleet saveFleet(Fleet fleet) {
        try {
            return fleetManager.persist(fleet);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to save fleet " + fleet.getName(), e);
        }
    }

    // verifying the existence of the fleet is done in the handler, among other verifications.
    public void updateFleet(Fleet fleet) {
        try {
            fleetManager.update(fleet);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to update fleet " + fleet.getId(), e);
        }
    }

    // verifying the existence of the fleet is done in the handler, among other verifications.
    public void deleteFleet(Fleet fleet) {
        try {
            fleetManager.remove(fleet);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to delete fleet " + fleet.getId(), e);
        }
    }
}
