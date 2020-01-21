package com.ractoc.eve.character.service;

import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdBlueprints200Ok;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class CharacterService {

    @Autowired
    private CharacterApi characterApi;

    public Stream<GetCharactersCharacterIdBlueprints200Ok> getBlueprintsForCharacter(Integer characterId, String accessToken) {
        try {
            return characterApi.getCharactersCharacterIdBlueprints(characterId, null, null, 1, accessToken).stream();
        } catch (ApiException e) {
            throw new ServiceException("Unable to retrieve Character Blueprints for character ID " + characterId, e);
        }
    }
}
