import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {TypeService} from "../../services/type.service";
import {Type} from "../../services/model/type";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";
import {FleetService} from "../../services/fleet.service";
import {Fleet} from "../../services/model/fleet";
import {LocalStorageService} from "../../services/local-storage.service";

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
    invited: new FormControl(false),
    registered: new FormControl(false),
    owned: new FormControl(false)
  });

  private types: Type[];
  private fleets: Fleet[];

  constructor(private router: Router,
              private userService: UserService,
              private typeService: TypeService,
              private fleetService: FleetService,
              private localStorageService: LocalStorageService) {
  }

  ngOnInit(): void {
    if (!this.userService.getCurrentUser()) {
      this.localStorageService.set('currentPage', '/fleet/find');
      this.router.navigateByUrl('/user/login');
    }
    this.loadTypes();
  }

  private loadTypes() {
    this.typeService.loadTypes().subscribe(
      (typeData: Type[]) => {
        console.log('setting types', typeData);
        this.types = typeData;
      }
    );
  }

  soSearch() {
    console.log('search parameters:', JSON.stringify(this.fleetForm.value));
    this.fleetService.find(this.fleetForm.value).subscribe(
      (fleetData: Fleet[]) => {
        if (fleetData && fleetData.length > 0) {
          this.fleets = fleetData;
        } else {
          console.log('no fleets found')
          // TODO: display error message in a nice popup
          // this.errorMessage = 'No fleets to display';
        }
      },
      err => {
        // TODO: display error message in a nice popup
        // this.errorMessage = err;
      }
    );
  }
}
