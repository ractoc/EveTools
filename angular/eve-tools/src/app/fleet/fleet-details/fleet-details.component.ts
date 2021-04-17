import {AfterViewChecked, Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";

import {Subscription} from "rxjs";

import {Fleet} from "../../services/model/fleet";
import {FleetService} from "../../services/fleet.service";
import {Type} from "../../services/model/type";
import {TypeService} from "../../services/type.service";
import {UserService} from "../../services/user.service";
import {LocalStorageService} from "../../services/local-storage.service";
import {DateUtil} from "../../services/date-util";
import {Role} from "../../services/model/role";
import {RoleService} from "../../services/role.service";
import {RoleDialogComponent} from "../role-dialog/role-dialog.component";
import {Invitation} from "../../services/model/invitation";
import {SearchCharCorpDialogComponent} from "../../search/search-char-corp-dialog/search-char-corp-dialog.component";
import {InvitationService} from "../../services/invitation.service";
import {CharacterService} from "../../services/character.service";
import {CorporationService} from "../../services/corporation.service";
import {Registration} from "../../services/model/registration";
import {RegistrationService} from "../../services/registration.service";
import {SearchUniverseDialogComponent} from "../../search/search-universe-dialog/search-universe-dialog.component";
import {UniverseService} from "../../services/universe.service";
import {Solarsystem} from "../../services/model/solarsystem";


@Component({
  selector: 'app-fleet-details',
  templateUrl: './fleet-details.component.html',
  styleUrls: ['./fleet-details.component.css']
})
export class FleetDetailsComponent implements OnInit, AfterViewChecked {

  subTitle: String;
  fleet: Fleet;
  types: Type[];
  fleetRoles: Role[];
  rallyPoint: Solarsystem;
  fleetInvitations: Invitation[];
  fleetRegistrations: Registration[];
  editing: boolean;
  owner: boolean;
  private routeListener$: Subscription;
  myInvite: Invitation;

  fleetForm = new FormGroup({
    id: new FormControl(),
    owner: new FormControl(),
    name: new FormControl(null, [
      Validators.required,
      Validators.minLength(4),
      Validators.maxLength(45)]),
    description: new FormControl(null, [
      Validators.required,
      Validators.minLength(4),
      Validators.maxLength(1000)]),
    type: new FormControl(null, [
      Validators.required]),
    roles: new FormControl(null),
    start: new FormControl(null, [
      Validators.required]),
    solarsystemName: new FormControl(null),
    restricted: new FormControl()
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private localStorageService: LocalStorageService,
    private fleetService: FleetService,
    private typeService: TypeService,
    private roleService: RoleService,
    private invitationService: InvitationService,
    private registrationService: RegistrationService,
    private characterService: CharacterService,
    private corporationService: CorporationService,
    private universeService: UniverseService,
    public dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    if (!this.userService.getCurrentUser()) {
      this.localStorageService.set('currentPage', this.router.url);
      this.router.navigateByUrl('/user/login');
    } else {
      this.routeListener$ = this.route.params
        .subscribe((params: any) => {
            if (params.id) {
              this.loadFleet(params.id);
            } else if (params.key) {
              this.loadInvitation(params.key);
            } else {
              this.subTitle = 'New Fleet';
              this.initFleetForm(undefined);
            }
          }
        );
      this.loadTypes();
    }
  }

  ngAfterViewChecked() {
    if (this.myInvite) {
      const itemToScrollTo = document.getElementById(String(this.myInvite.id));
      if (itemToScrollTo) {
        itemToScrollTo.scrollIntoView({behavior: "smooth"});
      }
    }
  }

  loadFleetRoles() {
    this.roleService.loadFleetRoles(this.fleet.id).subscribe(
      (roleData: Role[]) => {
        console.log('roleData', roleData);
        this.fleetRoles = roleData;
      }
    );
  }

  loadInvitations() {
    this.invitationService.loadInvitationsForFleet(this.fleet.id).subscribe(
      (invitationData: Invitation[]) => {
        this.fleetInvitations = invitationData.map(invitation => this.populateInvitation(invitation));
      }
    );
  }

  loadRegistrations() {
    this.registrationService.loadRegistrationsForFleet(this.fleet.id).subscribe(
      (registrationData: Registration[]) => {
        this.fleetRegistrations = registrationData.map(registration => this.populateRegistration(registration));
      }
    );
  }

  cleanDate() {
    let startDateTime = new Date(Number(Date.parse(this.fleetForm.value.start)));
    startDateTime.setSeconds(0);
    this.fleetForm.setValue(
      {
        id: this.fleetForm.value.id,
        owner: this.fleetForm.value.owner,
        name: this.fleetForm.value.name,
        description: this.fleetForm.value.description,
        type: this.fleetForm.value.type,
        roles: this.fleetForm.value.roles,
        start: startDateTime,
        solarsystemName: this.fleetForm.value.solarsystemName,
        restricted: this.fleetForm.value.restricted
      }
    );
  }

  saveFleet() {
    let startDate: Date = this.fleetForm.value.start;
    let fleet: Fleet = {
      id: this.fleetForm.value.id,
      locationId: undefined,
      owner: this.fleetForm.value.owner,
      duration: undefined,
      name: this.fleetForm.value.name,
      description: this.fleetForm.value.description,
      type: this.fleetForm.value.type,
      start: DateUtil.formatDate(startDate),
      solarsystemId: this.fleet ? this.fleet.solarsystemId : undefined,
      restricted: this.fleetForm.value.restricted
    };
    this.fleetService.saveFleet(fleet).subscribe(
      (fleetData: Fleet) => {
        if (fleetData) {
          this.router.navigateByUrl('/fleet/details/' + fleetData.id);
        } else if (this.editing) {
          this.fleet = fleet;
        }
        this.subTitle = 'Fleet Details';
        this.editing = false;
      });
  }

  editFleet() {
    this.fleetForm.controls.name.enable();
    this.fleetForm.controls.description.enable();
    this.fleetForm.controls.type.enable();
    this.fleetForm.controls.start.enable();
    this.fleetForm.controls.restricted.enable();
    this.editing = true;
    this.subTitle = 'Update Fleet';
  }

  cancel() {
    if (this.fleet) {
      this.subTitle = 'Fleet Details';
      this.initFleetForm(this.fleet);
      this.fleetForm.controls.solarsystemName.setValue(this.rallyPoint.name);
      this.fleet.solarsystemId = this.rallyPoint.id;
    }
  }

  compareById(t1: any, t2: any) {
    return t1 && t2 && t1.id === t2.id;
  }

  openRolesDialog() {
    const roleDialogRef = this.dialog.open(RoleDialogComponent, {
      data: {
        fleetId: this.fleet.id
      }
    });
    roleDialogRef.afterClosed().subscribe(selectedRole => {
      this.roleService.addRoleToFleet(selectedRole, this.fleet.id).subscribe(
        (roleData: Role[]) => {
          this.fleetRoles = roleData;
        }
      );
    });
  }

  removeRole(role: Role) {
    this.roleService.removeRoleFromFleet(role, this.fleet.id).subscribe(
      (roleData: Role[]) => {
        this.fleetRoles = roleData;
      }
    );
  }

  openSearchCharCorpDialog() {
    const searchCharCorpDialogRef = this.dialog.open(SearchCharCorpDialogComponent);
    searchCharCorpDialogRef.afterClosed().subscribe(searchResult => {
      this.invitationService.addInvitationToFleet(searchResult.id, searchResult.type, this.fleet.id).subscribe(
        (invitationData: Invitation[]) => {
          this.fleetInvitations = invitationData.map(invitation => this.populateInvitation(invitation));
        }
      );
    });
  }

  openSearchUniverseDialog() {
    const searchUniverseDialogRef = this.dialog.open(SearchUniverseDialogComponent);
    searchUniverseDialogRef.afterClosed().subscribe(searchResult => {
      console.log('search result: ', searchResult);
      this.fleet.solarsystemId = searchResult.id;
      this.fleetForm.controls.solarsystemName.setValue(searchResult.name);
    });
  }

  removeRallyPoint() {
    this.fleetForm.controls.solarsystemName.setValue(undefined);
    this.fleet.solarsystemId = undefined;
  }

  removeInvitation(invitation: Invitation) {
    this.invitationService.removeInvitation(invitation.id).subscribe(
      (invitationData: Invitation[]) => {
        this.fleetInvitations = invitationData.map(invitation => this.populateInvitation(invitation));
      }
    );
  }

  removeRegistration(registration: Registration) {
    this.registrationService.removeRegistration(registration.fleetId).subscribe(
      (registrationData: Registration[]) => {
        this.fleetRegistrations = registrationData.map(registration => this.populateRegistration(registration));
      }
    );
  }

  private loadFleet(fleetId: number) {
    this.fleetService.getFleet(fleetId).subscribe(
      (fleetData: Fleet) => {
        if (fleetData) {
          this.fleet = fleetData;
          this.subTitle = 'Fleet Details';
          this.initFleetForm(fleetData);
          this.loadInvitations();
          this.loadRegistrations();
          this.loadSolarsystem();
        }
      }
    );
  }

  private initFleetForm(fleet: Fleet) {
    if (fleet) {
      this.fleetForm.controls.id.setValue(fleet.id);
      this.fleetForm.controls.owner.setValue(fleet.owner);
      this.fleetForm.controls.name.setValue(fleet.name);
      this.fleetForm.controls.name.disable();
      this.fleetForm.controls.description.setValue(fleet.description);
      this.fleetForm.controls.description.disable();
      this.fleetForm.controls.type.setValue(fleet.type);
      this.fleetForm.controls.type.disable();
      this.fleetForm.controls.start.setValue(DateUtil.parseDate(fleet.start));
      this.fleetForm.controls.start.disable();
      this.fleetForm.controls.solarsystemName.disable();
      this.fleetForm.controls.restricted.setValue(fleet.restricted);
      this.fleetForm.controls.restricted.disable();

      this.owner = this.userService.getCurrentUser().characterId === fleet.owner;
      this.subTitle = 'Fleet Details';
      this.editing = false;
    } else {
      this.editFleet();
    }
  }

  private loadTypes() {
    this.typeService.loadTypes().subscribe(
      (typeData: Type[]) => {
        this.types = typeData;
      }
    );
  }

  private populateInvitation(invitation: Invitation) {
    if (invitation.type === 'character') {
      this.loadCharacterForInvitation(invitation);
    } else if (invitation.type === 'corporation') {
      this.loadCorporation(invitation);
    }
    return invitation;
  }

  private populateRegistration(registration: Registration) {
    this.loadCharacterForRegistration(registration);
    return registration;
  }

  private loadCharacterForInvitation(invitation: Invitation) {
    this.characterService.getCharacter(invitation.inviteeId).subscribe(character => {
      invitation.name = character.name;
      this.characterService.getPortrait(invitation.inviteeId).subscribe(portrait => {
        invitation.icon = portrait;
      })
    })
  }

  private loadCharacterForRegistration(registration: Registration) {
    this.characterService.getCharacter(registration.characterId).subscribe(character => {
      registration.name = character.name;
      this.characterService.getPortrait(registration.characterId).subscribe(portrait => {
        registration.icon = portrait;
      })
    })
  }

  private loadCorporation(invitation: Invitation) {
    this.corporationService.getCorporation(invitation.inviteeId).subscribe(corporation => {
      invitation.name = corporation.name;
      this.corporationService.getIcon(invitation.inviteeId).subscribe(portrait => {
        invitation.icon = portrait;
      })
    })
  }

  isMyInvite(invitation: Invitation) {
    if (invitation.type === 'character') {
      return invitation.inviteeId === this.userService.getCurrentUser().characterId;
    }
    if (invitation.type === 'corporation') {
      return invitation.inviteeId === this.userService.getCurrentUser().corporationId
        && (this.fleetRegistrations
          && !this.fleetRegistrations.find(registration => registration.characterId == this.userService.getCurrentUser().characterId));
    }
  }

  isMyRegistration(registration: Registration) {
    return registration.characterId === this.userService.getCurrentUser().characterId;
  }

  acceptInvitation(invitation: Invitation) {
    this.invitationService.acceptInvitation(invitation.id).subscribe(
      () => {
        this.router.navigateByUrl('/fleet/details/' + this.fleet.id);
      }
    );
  }

  denyInvitation(invitation: Invitation) {
    this.invitationService.denyInvitation(invitation.id).subscribe(
      () => {
        this.router.navigateByUrl('/fleet/details/' + this.fleet.id);
      }
    );
  }

  private loadInvitation(key: string) {
    this.invitationService.loadInvitation(key).subscribe(
      (invitationData: Invitation) => {
        this.myInvite = invitationData;
        this.loadFleet(invitationData.fleetId);
      }
    );
  }

  displayRegistrations() {
    return this.fleetRegistrations && this.fleetRegistrations.filter(registration => registration.accept)
  }

  private loadSolarsystem() {
    if (this.fleet.solarsystemId) {
      this.universeService.getSolarSystem(this.fleet.solarsystemId).subscribe(solarsystem => {
        this.fleetForm.controls.solarsystemName.setValue(solarsystem.name);
        this.rallyPoint = solarsystem;
      })
    } else {
      this.fleetForm.controls.solarsystemName.setValue(undefined);
    }
  }
}
