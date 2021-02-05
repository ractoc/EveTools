import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";

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
    id: new FormControl(),
    owner: new FormControl(),
    name: new FormControl('', [
      Validators.required,
      Validators.minLength(4),
      Validators.maxLength(45)]),
    description: new FormControl('', [
      Validators.required,
      Validators.minLength(4),
      Validators.maxLength(1000)]),
    type: new FormControl('', [
      Validators.required]),
    start: new FormControl('', [
      Validators.required]),
    restricted: new FormControl()
  });
  private fleet: Fleet;
  types: Type[];
  editing: boolean;
  update: boolean;
  owner: boolean;

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
          this.fleet = fleetData;
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
      this.update = true;
      this.editing = false;
    } else {
      this.fleetForm = new FormGroup({
        id: new FormControl(),
        owner: new FormControl(),
        name: new FormControl(),
        description: new FormControl(),
        type: new FormControl(),
        start: new FormControl(),
        restricted: new FormControl(false),
      });
      this.update = false;
      this.editFleet();
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
        id: this.fleetForm.value.id,
        owner: this.fleetForm.value.owner,
        name: this.fleetForm.value.name,
        description: this.fleetForm.value.description,
        type: this.fleetForm.value.type,
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
          this.fleetForm.controls.id.setValue(fleetData.id);
          this.fleetForm.controls.owner.setValue(fleetData.owner);
          this.owner = this.userService.getCurrentUser().characterId === fleet.owner;
          this.update = true;
          this.editing = true;
        }
      },
      err => {
      });
  }

  updateFleet() {

  }

  editFleet() {
    this.fleetForm.controls.name.enable();
    this.fleetForm.controls.description.enable();
    this.fleetForm.controls.type.enable();
    this.fleetForm.controls.start.enable();
    this.fleetForm.controls.restricted.enable();
    this.editing = true;
  }

  cancel() {
    if (this.update) {
      this.initFleetForm(this.fleet);
    }
  }

  compareType(t1: any, t2: any) {
    return t1.id === t2.id;
  }
}
