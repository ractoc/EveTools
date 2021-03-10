package com.ractoc.eve.user.service;

import com.ractoc.eve.domain.user.UserModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdRolesOk;
import com.ractoc.eve.user.db.user.eve_user.user.User;
import com.ractoc.eve.user.db.user.eve_user.user.UserImpl;
import com.ractoc.eve.user.db.user.eve_user.user.UserManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ractoc.eve.user.db.user.eve_user.user.generated.GeneratedUser.CHARACTER_ID;
import static com.ractoc.eve.user.db.user.eve_user.user.generated.GeneratedUser.EVE_STATE;

@Service
@Log4j2
public class UserService {

    private UserManager userManager;
    private CharacterApi characterApi;

    @Value("${sso.evetools-charid}")
    private Integer evetoolsCharId;

    @Autowired
    public UserService(UserManager userManager, CharacterApi characterApi) {
        this.userManager = userManager;
        this.characterApi = characterApi;
    }

    public String initializeUser() {
        log.trace("initiate user");
        User user = new UserImpl();
        user.setEveState(generateEveState());
        userManager.persist(user);
        return user.getEveState();
    }

    public void updateUser(User user) {
        log.trace("initiate user " + user);
        userManager.stream()
                .filter(User.CHARACTER_ID.equal(user.getCharacterId().orElseThrow(() -> new IllegalArgumentException("CharacterId")))
                        .and(EVE_STATE.notEqual(user.getEveState())))
                .forEach(userManager::remove);
        userManager.update(user);
    }

    public Optional<User> getUser(String eveState) {
        log.trace("get user for state " + eveState);
        return userManager.stream().filter(EVE_STATE.equal(eveState)).findAny();
    }

    public Optional<User> getUser(Integer charId) {
        log.trace("get user for id " + charId);
        return userManager.stream().filter(CHARACTER_ID.equal(charId)).findAny();
    }

    public void logoutUser(String eveState) {
        log.trace("logout user for state " + eveState);
        User user = getUser(eveState).orElseThrow(() -> new NoSuchEntryException("user not found or userState " + eveState));
        // eve tools user should not be removed from the database, only logged out of the client.
        if (user.getCharacterId().isPresent() && user.getCharacterId().getAsInt() != evetoolsCharId) {
            userManager.remove(user);
        }
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
