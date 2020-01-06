package com.ractoc.eve.user.service;

import com.ractoc.eve.user.db.user.eve_user.user.User;
import com.ractoc.eve.user.db.user.eve_user.user.UserImpl;
import com.ractoc.eve.user.db.user.eve_user.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.ractoc.eve.user.db.user.eve_user.user.generated.GeneratedUser.EVE_STATE;

@Service
public class UserService {

    private UserManager userManager;

    @Autowired
    public UserService(UserManager userManager) {
        this.userManager = userManager;
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

    private String generateEveState() {
        String eveState;
        do {
            eveState = UUID.randomUUID().toString();
        } while (getUser(eveState).isPresent());
        return eveState;
    }
}
