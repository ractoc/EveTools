package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.InvitationModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.Invite;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InviteMapper extends BaseMapper {
    InviteMapper INSTANCE = Mappers.getMapper(InviteMapper.class);

    InvitationModel dbToModel(Invite invite);
}
