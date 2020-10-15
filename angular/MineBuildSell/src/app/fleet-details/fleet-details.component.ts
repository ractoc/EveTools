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
import {EveIconService} from "../service/eve-icon.service";

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
  corporationRestricted: string;

  editFleet: boolean;
  displayPassword: boolean;
  savingFleet: boolean;
  invitations: InviteModel[];
  registrations: RegistrationModel[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private login: LoginComponent,
    private userService: UserService,
    private localStorageService: LocalStorageService,
    private fleetService: FleetService,
    private inviteService: InviteService,
    private eveIconService: EveIconService,
    private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
          if (params.id) {
            this.fleetService.getFleet(params.id).subscribe(
              (fleetData: FleetModel) => {
                if (fleetData) {
                  this.fleet = fleetData;
                  const start = JSON.parse(fleetData.start);
                  this.startDate = start.date;
                  this.startTime = start.time;
                  this.corporationRestricted = '' + fleetData.corporationRestricted;
                  this.loadInvitations();
                  // loadRegistrations();
                }
              },
              err => {
                if (err.status === 401) {
                  this.localStorageService.remove('eve-state');
                  if (params.id) {
                    this.localStorageService.set('currentPage', '/fleet/' + params.id);
                  } else {
                    this.localStorageService.set('currentPage', '/fleets');
                  }
                  this.router.navigateByUrl('/login');
                }
              }
            );
          } else {
            this.fleet = new FleetModel(undefined,
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
            this.corporationRestricted = 'false';
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
      this.fleet.corporationRestricted = this.corporationRestricted === 'true';
      this.fleetService.saveFleet(this.fleet).subscribe(
        () => {
          this.router.navigateByUrl('/fleets');
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

  open(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      if (result === 'Delete') {
        this.doDeleteFleet();
      }
    });
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
        this.invitations = undefined;
        this.loadInvitations();
      }
    );
  }

  private loadInvitations() {
    this.inviteService.getFleetInvites(this.fleet).subscribe(
      (inviteData: InviteModel[]) => {
        if (inviteData && inviteData.length > 0) {
          this.invitations = inviteData;
        }
      }
    );
  }

  getInviteeIcon(invitation: InviteModel) {
    if (!invitation.loadingIcon && !invitation.icon) {
      invitation.loadingIcon = true;
      if (invitation.corporationId) {
        this.eveIconService.getCorporationIcon(invitation.corporationId).toPromise().then(iconData => {
          invitation.icon = iconData.px64x64;
        });
      } else if (invitation.characterId) {
        this.eveIconService.getCharacterIcon(invitation.characterId).toPromise().then(iconData => {
          invitation.icon = iconData.px64x64;
        });
      }
    }
  }
}
