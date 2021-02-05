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
  fleetForm: FormGroup;
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
        id: new FormControl(fleet.id),
        owner: new FormControl(fleet.owner),
        name: new FormControl(fleet.name, [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(45)]),
        description: new FormControl(fleet.description, [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(1000)]),
        type: new FormControl(fleet.type, [
          Validators.required]),
        start: new FormControl(DateUtil.parseDate(fleet.start), [
          Validators.required]),
        restricted: new FormControl(fleet.restricted)
      });
      this.update = true;
      this.owner = this.userService.getCurrentUser().characterId === fleet.owner;
      this.cancel();
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
      this.fleetForm.controls.name.disable();
      this.fleetForm.controls.description.disable();
      this.fleetForm.controls.type.disable();
      this.fleetForm.controls.start.disable();
      this.fleetForm.controls.restricted.disable();
      this.editing = false;
    }
  }

  compareType(t1: any, t2: any) {
    return t1.id === t2.id;
  }
}
