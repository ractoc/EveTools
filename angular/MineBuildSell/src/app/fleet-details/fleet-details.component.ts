import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgbDateStruct, NgbModal, NgbTimeStruct} from '@ng-bootstrap/ng-bootstrap';
import {FleetModel} from '../shared/model/fleet-model';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {FleetService} from '../service/fleet.service';
import {LoginComponent} from '../login/login.component';
import {UserService} from '../service/user.service';
import {LocalStorageService} from '../service/local-storage.service';
import {InviteModel} from '../shared/model/invite-model';
import {RegistrationModel} from '../shared/model/Registration-model';
import {InviteService} from '../service/invite.service';
import {EveIconService} from '../service/eve-icon.service';
import {RegistrationService} from '../service/registration.service';
import {TypeModel} from '../shared/model/type-model';
import {TypeService} from '../service/type.service';
import {RoleModel} from '../shared/model/role.model';
import {SearchResultModel} from '../shared/model/searchResult.model';
import {CharacterService} from '../service/character.service';
import {CorporationService} from '../service/corporation.service';

@Component({
  selector: 'app-fleet-details',
  templateUrl: './fleet-details.component.html',
  styleUrls: ['./fleet-details.component.css']
})
export class FleetDetailsComponent implements OnInit, OnDestroy {

  fleet: FleetModel;
  private routeListener$: Subscription;

  startDate: NgbDateStruct;
  startTime: NgbTimeStruct;
  restricted: string;
  types: TypeModel[];

  editFleet: boolean;
  displayPassword: boolean;
  savingFleet: boolean;
  invitations: InviteModel[] = [];
  registrations: RegistrationModel[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private login: LoginComponent,
    private userService: UserService,
    private localStorageService: LocalStorageService,
    private fleetService: FleetService,
    private inviteService: InviteService,
    private registrationService: RegistrationService,
    private eveIconService: EveIconService,
    private typeService: TypeService,
    private modalService: NgbModal,
    private characterService: CharacterService,
    private corporationService: CorporationService) {
  }

  ngOnInit(): void {
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
          if (params.id) {
            this.loadFleet(params.id);
          } else {
            this.initFleetDetails();
          }
        }
      );
    this.loadTypes();
  }

  private initFleetDetails() {
    this.fleet = new FleetModel(undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      -1,
      false,
      [],
      undefined);
    const currentDate = new Date();
    this.startDate = {
      year: currentDate.getFullYear(),
      month: currentDate.getMonth() + 1,
      day: currentDate.getDate()
    };
    this.startTime = {hour: 0, minute: 0, second: 0};
    this.restricted = 'false';
  }

  private loadFleet(fleetId: number) {
    this.fleetService.getFleet(fleetId).subscribe(
      (fleetData: FleetModel) => {
        if (fleetData) {
          this.fleet = fleetData;
          const start = JSON.parse(fleetData.start);
          this.startDate = start.date;
          this.startTime = start.time;
          this.restricted = '' + fleetData.restricted;
          this.loadInvitations();
          this.loadRegistrations();
        }
      },
      err => {
        if (err.status === 401) {
          this.localStorageService.remove('eve-state');
          if (fleetId) {
            this.localStorageService.set('currentPage', '/fleet/' + fleetId);
          } else {
            this.localStorageService.set('currentPage', '/fleets');
          }
          this.router.navigateByUrl('/login');
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.routeListener$.unsubscribe();
  }

  doSaveFleet() {
    if (this.validateInput()) {
      this.savingFleet = true;
      this.fleet.start = JSON.stringify({date: this.startDate, time: this.startTime});
      this.fleet.restricted = this.restricted === 'true';
      this.fleetService.saveFleet(this.fleet).subscribe(
        (fleetData: FleetModel) => {
          this.fleet = fleetData;
          this.router.navigateByUrl('/fleet/' + this.fleet.id);
          this.savingFleet = false;
        },
        err => {
          this.savingFleet = false;
          if (err.status === 401) {
            this.localStorageService.remove('eve-state');
            if (this.fleet.id) {
              this.localStorageService.set('currentPage', '/fleet/' + this.fleet.id);
            } else {
              this.localStorageService.set('currentPage', '/fleet');
            }
            this.router.navigateByUrl('/login');
          }
        }
      );
      this.editFleet = false;
    }
  }

  openDelete(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      if (result === 'Delete') {
        this.doDeleteFleet();
      }
    });
  }

  openSearch(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

  searchResult(searchResult: SearchResultModel) {
    this.inviteService.addInvitation(this.fleet.id, searchResult.type, searchResult.id, searchResult.name).subscribe(inviteKey => {
      this.inviteService.getInvite(inviteKey).subscribe(invite => {
        this.addInviteToList(invite);
      });
    });
    this.modalService.dismissAll();
  }

  private addInviteToList(invite: InviteModel) {
    this.invitations.push(invite);
    if (invite.type === 'character') {
      this.characterService.getPortrait(invite.id).subscribe(portrait => {
        invite.icon = portrait;
      });
    } else if (invite.type === 'corporation') {
      this.corporationService.getIcon(invite.id).subscribe(icon => {
        invite.icon = icon;
      });
    }
  }

  validateInput() {
    return this.fleet && this.fleet.name && this.startDate && this.startTime;
  }

  editable() {
    return !(this.fleet && this.fleet.id) || this.editFleet;
  }

  owner() {
    return this.fleet.owner === this.userService.getCurrentUser().characterId;
  }

  doEditFleet() {
    this.editFleet = true;
  }

  doDeleteFleet() {
    this.fleetService.deleteFleet(this.fleet).subscribe(
      () => {
        this.router.navigateByUrl('/fleets');
      },
      err => {
        if (err.status === 401) {
          this.localStorageService.remove('eve-state');
          if (this.fleet.id) {
            this.localStorageService.set('currentPage', '/fleet/' + this.fleet.id);
          } else {
            this.localStorageService.set('currentPage', '/fleet');
          }
          this.router.navigateByUrl('/login');
        }
      }
    );
  }

  deleteInvitation(invitation: InviteModel) {
    this.inviteService.deleteInvitation(invitation.key).subscribe(
      () => {
        this.invitations = [];
        this.loadInvitations();
      }
    );
  }

  deleteRegistration(registration: RegistrationModel) {
    this.registrationService.deleteRegistration(registration.fleetId).subscribe(
      () => {
        this.registrations = [];
        this.loadRegistrations();
      }
    );
  }

  private loadInvitations() {
    this.inviteService.getFleetInvites(this.fleet).subscribe(
      (inviteData: InviteModel[]) => {
        if (inviteData && inviteData.length > 0) {
          for (const invite of inviteData) {
            this.addInviteToList(invite);
          }
        } else {
          this.invitations = [];
        }
      }
    );
  }

  private loadRegistrations() {
    this.registrationService.getFleetRegistrations(this.fleet).subscribe(
      (registrationData: RegistrationModel[]) => {
        if (registrationData && registrationData.length > 0) {
          this.registrations = registrationData;
        }
      }
    );
  }

  getRegistrationIcon(registration: RegistrationModel) {
    if (!registration.loadingIcon && !registration.icon) {
      registration.loadingIcon = true;
      if (registration.characterId) {
        this.eveIconService.getCharacterIcon(registration.characterId).toPromise().then(iconData => {
          registration.icon = iconData.px64x64;
        });
      }
    }
  }

  registrant(registration: RegistrationModel) {
    return registration.characterId === this.userService.getCurrentUser().characterId;
  }

  registered() {
    return this.registrations && this.registrations.find(reg => this.registrant(reg));
  }

  doRegister() {
    this.registrationService.registerForFleet(this.fleet.id).subscribe(
      (registrationData: RegistrationModel) => {
        this.router.navigateByUrl('/fleets/registration/' + registrationData.fleetId + '/' + registrationData.characterId);
      }
    );
  }

  private loadTypes() {
    this.typeService.loadTypes().subscribe(
      (typeData: TypeModel[]) => {
        console.log('setting types', typeData);
        this.types = typeData;
      }
    );
  }

  onSelectRole(role: RoleModel, checked: boolean) {
    console.log(role.id + '=' + checked);
    if (!this.fleet.roles) {
      this.fleet.roles = [];
    }
    if (checked) {
      if (!this.fleet.roles.some(r => r.id === role.id)) {
        this.fleet.roles.push(role);
      }
    } else {
      this.fleet.roles = this.fleet.roles.filter(r => r.id !== role.id);
    }
  }

  roleSelected(roleId: number) {
    if (this.fleet.roles) {
      for (const fleetRole of this.fleet.roles) {
        if (fleetRole.id === roleId) {
          return true;
        }
      }
    }
    return undefined;
  }

  roleAmount(roleId: number) {
    if (this.fleet.roles) {
      for (const fleetRole of this.fleet.roles) {
        if (fleetRole.id === roleId) {
          return fleetRole.amount ? fleetRole.amount : 0;
        }
      }
    }
    return 0;
  }

  setFleetRoleAmount(roleId: number, amount: number) {
    if (this.fleet.roles) {
      for (const fleetRole of this.fleet.roles) {
        if (fleetRole.id === roleId) {
          fleetRole.amount = amount;
        }
      }
    }
  }
}
