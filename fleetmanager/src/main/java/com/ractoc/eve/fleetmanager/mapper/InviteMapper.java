package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.InviteModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.Invites;
import com.speedment.common.tuple.Tuple2;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InviteMapper extends BaseMapper {
    InviteMapper INSTANCE = Mappers.getMapper(InviteMapper.class);

    InviteModel dbToModel(Invites invite);

    static InviteModel mapInvitesWithFleet(Invites invites, Fleet fleet) {
        InviteModel invite = INSTANCE.dbToModel(invites);
        invite.setFleet(FleetMapper.INSTANCE.dbToModel(fleet));
        return invite;
    }

    default InviteModel joinToModel(Tuple2<Invites, Fleet> join) {
        InviteModel model = dbToModel(join.get0());
        model.setFleet(FleetMapper.INSTANCE.dbToModel(join.get1()));
        return model;
    }
}
