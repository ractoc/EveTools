package com.ractoc.eve.character.handler;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.ItemResourceApi;
import com.ractoc.eve.character.service.CharacterService;
import com.ractoc.eve.character.service.ServiceException;
import com.ractoc.eve.domain.character.BlueprintListModel;
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
    private ItemResourceApi itemResourceApi;

    @Autowired
    public CharacterHandler(CharacterService characterService, ItemResourceApi itemResourceApi) {
        this.characterService = characterService;
        this.itemResourceApi = itemResourceApi;
    }

    public List<BlueprintListModel> getBlueprintsForCharacter(EveUserDetails eveUserDetails) {
        return characterService.getBlueprintsForCharacter(eveUserDetails.getCharId(), eveUserDetails.getAccessToken())
                .map(INSTANCE::esiToModel)
                .map(this::addNameToBlueprint)
                .collect(Collectors.toList());
    }

    private BlueprintListModel addNameToBlueprint(BlueprintListModel blueprintListModel) {
        try {
            String itemName = itemResourceApi.getItemName(blueprintListModel.getId()).getName();
            blueprintListModel.setName(itemName);
            return blueprintListModel;
        } catch (ApiException e) {
            e.printStackTrace();
            throw new ServiceException("Unable to retrieve name for item " + blueprintListModel.getId(), e);
        }
    }
}
