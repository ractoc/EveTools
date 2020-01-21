package com.ractoc.eve.character.handler;

import com.ractoc.eve.character.service.CharacterService;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.user.filter.EveUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

import static com.ractoc.eve.character.mapper.BlueprintMapper.INSTANCE;

@Service
@Validated
public class CharacterHandler {

    private final CharacterService characterService;

    @Autowired
    public CharacterHandler(CharacterService characterService) {
        this.characterService = characterService;
    }

    public List<BlueprintModel> getBlueprintsForCharacter(EveUserDetails eveUserDetails) {
        System.out.println(eveUserDetails);
        return characterService.getBlueprintsForCharacter(eveUserDetails.getCharId(), eveUserDetails.getAccessToken())
                .map(INSTANCE::esiToModel)
                .collect(Collectors.toList());
    }
}
