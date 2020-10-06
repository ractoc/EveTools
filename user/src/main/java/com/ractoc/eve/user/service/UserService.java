package com.ractoc.eve.user.service;

import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdRolesOk;
import com.ractoc.eve.user.db.user.eve_user.user.User;
import com.ractoc.eve.user.db.user.eve_user.user.UserImpl;
import com.ractoc.eve.user.db.user.eve_user.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ractoc.eve.user.db.user.eve_user.user.generated.GeneratedUser.EVE_STATE;

@Service
public class UserService {

    private UserManager userManager;
    private CharacterApi characterApi;

    @Autowired
    public UserService(UserManager userManager, CharacterApi characterApi) {
        this.userManager = userManager;
        this.characterApi = characterApi;
    }

    public String initializeUser(String remoteIP) {
        User user = new UserImpl();
        user.setEveState(generateEveState());
        user.setIpAddress(remoteIP);

        userManager.persist(user);

        return user.getEveState();
    }

    public void updateUser(User user) {
        userManager.update(user);
    }

    public Optional<User> getUser(String eveState) {
        return userManager.stream().filter(EVE_STATE.equal(eveState)).findAny();
    }

    public void switchUser(String eveState) {
        User user = getUser(eveState).orElseThrow(() -> new NoSuchEntryException("user not found or userState " + eveState));
        userManager.remove(user);
    }

    private String generateEveState() {
        String eveState;
        do {
            eveState = UUID.randomUUID().toString();
        } while (getUser(eveState).isPresent());
        return eveState;
    }

    public List<GetCharactersCharacterIdRolesOk.RolesEnum> getRoles(int characterId, String accessToken) throws ApiException {
        return characterApi.getCharactersCharacterIdRoles(characterId, null, null, accessToken).getRoles();
    }
}
