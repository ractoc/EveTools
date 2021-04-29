import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {TypeService} from "../../services/type.service";
import {Type} from "../../services/model/type";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";
import {FleetService} from "../../services/fleet.service";
import {Fleet} from "../../services/model/fleet";
import {LocalStorageService} from "../../services/local-storage.service";
import {Role} from "../../services/model/role";
import {RoleService} from "../../services/role.service";
import {DateUtil} from "../../services/date-util";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-fleet-finder',
  templateUrl: './fleet-finder.component.html',
  styleUrls: ['./fleet-finder.component.css']
})
export class FleetFinderComponent implements OnInit {

  fleetForm = new FormGroup({
    start: new FormControl(),
    end: new FormControl(),
    fleetTypes: new FormControl(),
    fleetRoles: new FormControl(),
    invited: new FormControl(false),
    registered: new FormControl(false),
    owned: new FormControl(false)
  });
  displayedColumns: string[] = ['name', 'start', 'type', 'restricted'];
  types: Type[];
  roles: Role[];
  fleets: Fleet[];
  ownedChecked: boolean;

  constructor(private router: Router,
              private userService: UserService,
              private typeService: TypeService,
              private roleService: RoleService,
              private fleetService: FleetService,
              private localStorageService: LocalStorageService,
              private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    if (!this.userService.getCurrentUser()) {
      this.localStorageService.set('currentPage', '/fleet/find');
      this.router.navigateByUrl('/user/login');
    } else {
      const formData: any = this.localStorageService.get('searchParams');
      this.loadTypes();
      this.loadRoles();
      if (formData) {
        this.fleetForm.setValue(JSON.parse(formData));
        this.doSearch();
        this.localStorageService.remove('searchParams')
      }
    }
  }

  convertStartToDate(start: string) {
    return DateUtil.parseDate(start);
  }

  doSearch() {
    this.hideError();
    this.fleets = undefined;
    this.fleetService.find(this.fleetForm.value).subscribe(
      (fleetData: Fleet[]) => {
        if (fleetData && fleetData.length > 0) {
          this.fleets = fleetData;
        } else {
          this.showError('No fleets to display');
        }
      },
      err => {
        this.showError('There was a problem executing your search');
      }
    );
  }

  showError(msg: string) {
    this.snackBar.open(msg, '', {
      duration: 30000
    });
  }

  hideError() {
    this.snackBar.dismiss();
  }

  showFleetDetails(fleet: Fleet) {
    this.localStorageService.set('searchParams', JSON.stringify(this.fleetForm.value));
    this.router.navigateByUrl('/fleet/details/' + fleet.id);
  }

  compareById(t1: any, t2: any) {
    return t1 && t2 && t1.id === t2.id;
  }

  changeCheck(field: string, checked: boolean) {
    if (checked) {
      if (field === 'invited') {
        this.fleetForm.controls.registered.setValue(false);
        this.fleetForm.controls.owned.setValue(false);
      } else if (field === 'registered') {
        this.fleetForm.controls.invited.setValue(false);
        this.fleetForm.controls.owned.setValue(false);
      } else if (field === 'owned') {
        this.fleetForm.controls.invited.setValue(false);
        this.fleetForm.controls.registered.setValue(false);
      }
    }
  }

  private loadTypes() {
    this.typeService.loadTypes().subscribe(
      (typeData: Type[]) => {
        this.types = typeData;
      }
    );
  }

  private loadRoles() {
    this.roleService.loadRoles(undefined).subscribe(
      (roleData: Role[]) => {
        this.roles = roleData;
      }
    );
  }
}
