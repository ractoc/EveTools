import {Component, OnInit} from '@angular/core';
import {FleetModel} from '../shared/model/fleet-model';
import {FleetService} from '../service/fleet.service';
import {Router} from '@angular/router';
import {LoginComponent} from '../login/login.component';
import {UserService} from '../service/user.service';
import {LocalStorageService} from '../service/local-storage.service';

@Component({
  selector: 'app-fleet-list',
  templateUrl: './fleet-list.component.html',
  styleUrls: ['./fleet-list.component.css']
})
export class FleetListComponent implements OnInit {

  fleets: FleetModel[];
  errorMessage: string;

  constructor(private fleetService: FleetService,
              private router: Router,
              private login: LoginComponent,
              private userService: UserService,
              private localStorageService: LocalStorageService) {
  }

  ngOnInit(): void {
    this.fleetService.getFleets().subscribe(
      (fleetData: FleetModel[]) => {
        if (fleetData && fleetData.length > 0) {
          this.fleets = fleetData;
        } else {
          this.errorMessage = 'No fleets to display';
        }
      },
      err => {
        this.errorMessage = err;
        if (err.status === 401) {
          this.localStorageService.remove('eve-state');
          this.localStorageService.set('currentPage', '/fleets');
          this.router.navigateByUrl('/login');
        }
      }
    );
  }

  fleetClass(fleet: FleetModel) {
    return {
      'list-group-item': true,
      'list-group-item-action': true,
    };
  }

}