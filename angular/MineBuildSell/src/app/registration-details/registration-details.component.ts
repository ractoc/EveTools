import {Component, OnInit} from '@angular/core';
import {FleetModel} from '../shared/model/fleet-model';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {LocalStorageService} from '../service/local-storage.service';
import {FleetService} from '../service/fleet.service';
import {NgbDateStruct, NgbTimeStruct} from '@ng-bootstrap/ng-bootstrap';
import {RegistrationService} from '../service/registration.service';
import {RegistrationModel} from '../shared/model/Registration-model';
import {RoleModel} from '../shared/model/role.model';
import {UserService} from '../service/user.service';

@Component({
  selector: 'app-registration-details',
  templateUrl: './registration-details.component.html',
  styleUrls: ['./registration-details.component.css']
})
export class RegistrationDetailsComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private localStorageService: LocalStorageService,
    private fleetService: FleetService,
    private registrationService: RegistrationService,
    private userService: UserService
  ) {
  }

  fleet: FleetModel;
  registration: RegistrationModel;

  private routeListener$: Subscription;

  fleetStartDate: NgbDateStruct;
  fleetStartTime: NgbTimeStruct;
  pilotStartDate: NgbDateStruct;
  pilotStartTime: NgbTimeStruct;
  pilotEndDate: NgbDateStruct;
  pilotEndTime: NgbTimeStruct;

  private static getCurrentDateTime() {
    const now = new Date();
    return {
      date: {
        day: now.getDay(),
        month: now.getMonth(),
        year: now.getFullYear()
      },
      time: {
        hour: now.getHours(),
        minute: now.getMinutes()
      }
    };
  }

  ngOnInit(): void {
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
          if (params.fleetId && params.charId) {
            this.loadFleet(params.fleetId, params.charId);
          } else {
            console.error('missing mandatory fleet id');
          }
        }
      );
  }

  private loadFleet(fleetId: number, charId: number) {
    this.fleetService.getFleet(fleetId).subscribe(
      (fleetData: FleetModel) => {
        if (fleetData) {
          this.fleet = fleetData;
          const start = JSON.parse(fleetData.start);
          this.fleetStartDate = start.date;
          this.fleetStartTime = start.time;
          console.log('fleet', this.fleet);
          this.loadRegistration(fleetId, charId);
        }
      },
      err => {
        if (err.status === 401) {
          this.localStorageService.remove('eve-state');
          this.localStorageService.set('currentPage', '/fleets/registration/' + fleetId + '/' + charId);
          this.router.navigateByUrl('/login');
        }
      }
    );
  }

  private loadRegistration(fleetId: number, charId: number) {
    this.registrationService.getFleetRegistrationUser(fleetId, charId).subscribe(
      (registrationData: RegistrationModel) => {
        this.extractRegistrationData(registrationData);
      }
    );
  }

  roleSelected(id: number) {
    return this.registration.roleId === id;
  }

  onSelectRole(role: RoleModel) {
    this.registration.roleId = role.id;
    this.updateRegistration();
  }

  start() {
    this.registration.start = JSON.stringify(RegistrationDetailsComponent.getCurrentDateTime());
    this.registration.end = undefined;
    this.updateRegistration();
  }

  end() {
    this.registration.end = JSON.stringify(RegistrationDetailsComponent.getCurrentDateTime());
    this.updateRegistration();
  }

  private updateRegistration() {
    this.registrationService.updateRegistration(this.registration).subscribe(
      (registrationData: RegistrationModel) => {
        this.extractRegistrationData(registrationData);
      }
    );
  }

  private extractRegistrationData(reg: RegistrationModel) {
    if (reg) {
      this.registration = reg;
      console.log('registration', this.registration);
      // reset date / times
      this.pilotStartDate = undefined;
      this.pilotStartTime = undefined;
      this.pilotEndDate = undefined;
      this.pilotEndTime = undefined;
      if (reg.start !== undefined) {
        const start = JSON.parse(reg.start);
        if (start) {
          this.pilotStartDate = start.date;
          this.pilotStartTime = start.time;
        }
      }
      if (reg.end !== undefined) {
        const end = JSON.parse(reg.end);
        if (end) {
          this.pilotEndDate = end.date;
          this.pilotEndTime = end.time;
        }
      }
    } else {
      console.error('no registration found for fleet');
    }
  }

  registrant() {
    return this.userService.getCurrentUser().characterId === this.registration.characterId;
  }

  role(roleId: number) {
    return this.fleet.roles.find(f => f.id === roleId);
  }
}
