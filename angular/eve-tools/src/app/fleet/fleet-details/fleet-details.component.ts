import {Component, OnInit} from '@angular/core';
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
import {InvitationDialogComponent} from "../invitation-dialog/invitation-dialog.component";
import {InvitationService} from "../../services/invitation.service";
import {CharacterService} from "../../services/character.service";
import {CorporationService} from "../../services/corporation.service";


@Component({
  selector: 'app-fleet-details',
  templateUrl: './fleet-details.component.html',
  styleUrls: ['./fleet-details.component.css']
})
export class FleetDetailsComponent implements OnInit {

  subTitle: String;
  fleet: Fleet;
  types: Type[];
  fleetRoles: Role[];
  fleetInvitations: Invitation[];
  editing: boolean;
  owner: boolean;
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
    restricted: new FormControl()
  });
  private routeListener$: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private localStorageService: LocalStorageService,
    private fleetService: FleetService,
    private typeService: TypeService,
    private roleService: RoleService,
    private invitationService: InvitationService,
    private characterService: CharacterService,
    private corporationService: CorporationService,
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
            } else {
              this.subTitle = 'New Fleet';
              this.initFleetForm(undefined);
            }
          }
        );
      this.loadTypes();
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
      restricted: this.fleetForm.value.restricted
    };
    console.log('fleet', fleet);
    this.fleetService.saveFleet(fleet).subscribe(
      (fleetData: Fleet) => {
        if (fleetData) {
          this.fleet = fleetData;
          this.fleetForm.controls.id.setValue(fleetData.id);
          this.fleetForm.controls.owner.setValue(fleetData.owner);
          this.owner = this.userService.getCurrentUser().characterId === fleet.owner;
          this.editing = true;
        }
      },
      err => {
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

  openInvitationsDialog() {
    const invitationDialogRef = this.dialog.open(InvitationDialogComponent, {
      data: {
        fleetId: this.fleet.id
      }
    });
    invitationDialogRef.afterClosed().subscribe(searchResult => {
      this.invitationService.addInvitationToFleet(searchResult.id, searchResult.type, this.fleet.id).subscribe(
        (invitationData: Invitation[]) => {
          this.fleetInvitations = invitationData.map(invitation => this.populateInvitation(invitation));
        }
      );
    });
  }

  removeInvitation(invitation: Invitation) {
    this.invitationService.removeInvitation(invitation.id).subscribe(
      (invitationData: Invitation[]) => {
        this.fleetInvitations = invitationData.map(invitation => this.populateInvitation(invitation));
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
        }
      },
      err => {
      }
    );
  }

  private initFleetForm(fleet: Fleet) {
    if (fleet) {
      this.fleetForm.controls.id.setValue(fleet.id);
      this.fleetForm.controls.owner.setValue(fleet.owner);
      this.fleetForm.controls.name.setValue(fleet.name);
      this.fleetForm.controls.name.disable();
      this.fleetForm.controls.description.setValue(fleet.id);
      this.fleetForm.controls.description.disable();
      this.fleetForm.controls.type.setValue(fleet.type);
      this.fleetForm.controls.type.disable();
      this.fleetForm.controls.start.setValue(DateUtil.parseDate(fleet.start));
      this.fleetForm.controls.start.disable();
      this.fleetForm.controls.restricted.setValue(fleet.restricted);
      this.fleetForm.controls.restricted.disable();

      this.owner = this.userService.getCurrentUser().characterId === fleet.owner;
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
      this.loadCharacter(invitation);
    } else if (invitation.type === 'corporation') {
      this.loadCorporation(invitation);
    }
    return invitation;
  }

  private loadCharacter(invitation: Invitation) {
    this.characterService.getCharacter(invitation.inviteeId).subscribe(character => {
      invitation.name = character.name;
      this.characterService.getPortrait(invitation.inviteeId).subscribe(portrait => {
        invitation.icon = portrait;
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
}
