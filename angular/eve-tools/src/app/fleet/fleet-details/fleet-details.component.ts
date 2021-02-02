import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl, FormGroup} from "@angular/forms";

import {Fleet} from "../../services/model/fleet";
import {FleetService} from "../../services/fleet.service";
import {Type} from "../../services/model/type";
import {TypeService} from "../../services/type.service";
import {UserService} from "../../services/user.service";
import {LocalStorageService} from "../../services/local-storage.service";
import {Subscription} from "rxjs";
import {DateUtil} from "../../services/date-util";

@Component({
  selector: 'app-fleet-details',
  templateUrl: './fleet-details.component.html',
  styleUrls: ['./fleet-details.component.css']
})
export class FleetDetailsComponent implements OnInit {

  private routeListener$: Subscription;

  subTitle: String;
  fleetForm = new FormGroup({
    name: new FormControl(),
    description: new FormControl(),
    type: new FormControl(),
    start: new FormControl(),
    restricted: new FormControl(false),
  });
  types: Type[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private localStorageService: LocalStorageService,
    private fleetService: FleetService,
    private typeService: TypeService
  ) {
  }

  ngOnInit(): void {
    if (!this.userService.getCurrentUser()) {
      this.localStorageService.set('currentPage', this.router.url);
      this.router.navigateByUrl('/user/login');
    }
    this.subTitle = 'New Fleet';
    this.initFleetForm(undefined);
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
          if (params.id) {
            this.loadFleet(params.id);
          } else {
            this.initFleetForm(undefined);
          }
        }
      );
    this.loadTypes();
  }

  private loadFleet(fleetId: number) {
    this.fleetService.getFleet(fleetId).subscribe(
      (fleetData: Fleet) => {
        if (fleetData) {
          this.initFleetForm(fleetData);
        }
      },
      err => {
      }
    );
  }

  private initFleetForm(fleet: Fleet) {
    if (fleet) {
      this.fleetForm = new FormGroup({
        name: new FormControl(fleet.name),
        description: new FormControl(fleet.description),
        type: new FormControl(fleet.type),
        start: new FormControl(DateUtil.parseDate(fleet.start)),
        restricted: new FormControl(fleet.restricted)
      });
    }
  }

  private loadTypes() {
    this.typeService.loadTypes().subscribe(
      (typeData: Type[]) => {
        console.log('setting types', typeData);
        this.types = typeData;
      }
    );
  }

  cleanDate() {
    let startDateTime = new Date(Number(Date.parse(this.fleetForm.value.start)));
    startDateTime.setSeconds(0);
    this.fleetForm.setValue(
      {
        name: this.fleetForm.value.name,
        description: this.fleetForm.value.description,
        type: this.fleetForm.value.type,
        start: startDateTime,
        restricted: this.fleetForm.value.restricted
      }
    );
  }

  createFleet() {
    console.log('fleet', this.fleetForm.value);
    let startDate: Date = this.fleetForm.value.start;
    let fleet: Fleet = {
      id: undefined,
      locationId: undefined,
      owner: undefined,
      typeId: undefined,
      duration: undefined,
      roles: undefined,
      invitations: undefined,
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
          this.initFleetForm(fleetData);
        }
      },
      err => {
      });
  }
  //
  // parseDate(start: string): Date {
  //   let startJson = JSON.parse(start);
  //   let dateString =
  //     startJson.date.year + '-' + startJson.date.month + '-' + startJson.date.day +
  //     'T'
  //     + startJson.time.hour + ':' + startJson.time.minute + ':' + startJson.time.second
  //   console.log('dateString', dateString);
  //   let startDate: Date = new Date(Date.UTC(startJson.date.year, startJson.date.month - 1, startJson.date.day, startJson.time.hour, startJson.time.minute, startJson.time.second));
  //   console.log('startDate', startDate);
  //   return startDate;
  // }
  //
  // formatDate(start: Date): string {
  //   return "{\"date\":{\"year\":" +
  //     start.getFullYear() +
  //     ",\"month\":" +
  //     (start.getMonth() + 1) +
  //     ",\"day\":" +
  //     start.getDate() +
  //     "},\"time\":{\"hour\":" +
  //     start.getUTCHours() +
  //     ",\"minute\":" +
  //     start.getMinutes() +
  //     ",\"second\":" +
  //     start.getSeconds() +
  //     "}}";
  // }

}
