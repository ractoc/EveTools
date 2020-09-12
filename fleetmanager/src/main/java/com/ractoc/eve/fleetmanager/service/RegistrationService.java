package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.RegistrationsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final RegistrationsManager registrationsManager;

    @Autowired
    public RegistrationService(RegistrationsManager registrationsManager) {
        this.registrationsManager = registrationsManager;
    }
}
