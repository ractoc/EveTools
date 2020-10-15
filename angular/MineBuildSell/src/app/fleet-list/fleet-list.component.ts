import {Component, OnInit} from '@angular/core';
import {FleetModel} from '../shared/model/fleet-model';
import {FleetService} from '../service/fleet.service';
import {ActivatedRoute, Router} from '@angular/router';
import {LoginComponent} from '../login/login.component';
import {UserService} from '../service/user.service';
import {LocalStorageService} from '../service/local-storage.service';
import {Subscription} from "rxjs";

@Component({
  selector: 'app-fleet-list',
  templateUrl: './fleet-list.component.html',
  styleUrls: ['./fleet-list.component.css']
})
export class FleetListComponent implements OnInit {

  fleets: FleetModel[];
  errorMessage: string;
  private routeListener$: Subscription;

  constructor(
    private route: ActivatedRoute,
    private fleetService: FleetService,
    private router: Router,
    private login: LoginComponent,
    private userService: UserService,
    private localStorageService: LocalStorageService) {
  }

  ngOnInit(): void {
    this.routeListener$ = this.route.data
      .subscribe((data: any) => {
        this.fleetService.getFleets(data.owned, data.active).subscribe(
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
      });
  }

  fleetClass(fleet: FleetModel) {
    return {
      'list-group-item': true,
      'list-group-item-action': true,
    };
  }

  displayFleetStart(fleet: FleetModel) {
    const start = JSON.parse(fleet.start);
    return this.toDoubleDigits(start.date.day) + '-' +
      this.toDoubleDigits(start.date.month) + '-' +
      start.date.year + ' ' +
      this.toDoubleDigits(start.time.hour) + ':' +
      this.toDoubleDigits(start.time.minute);
  }

  toDoubleDigits(digits: number) {
    if (digits < 10) {
      return '0' + digits;
    }
    return digits;
  }

}
