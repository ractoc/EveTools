import {Component, OnDestroy, OnInit} from '@angular/core';
import {ModalDismissReasons, NgbDateStruct, NgbModal, NgbTimeStruct} from '@ng-bootstrap/ng-bootstrap';
import {FleetModel} from '../shared/model/fleet-model';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {FleetService} from '../service/fleet.service';
import {LoginComponent} from '../login/login.component';
import {UserService} from '../service/user.service';
import {LocalStorageService} from '../service/local-storage.service';

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

  editFleet: boolean;
  closeResult: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private login: LoginComponent,
    private userService: UserService,
    private localStorageService: LocalStorageService,
    private fleetService: FleetService,
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
                }
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
          } else {
            this.fleet = new FleetModel(undefined, undefined, undefined, undefined, undefined, undefined, -1);
            const currentDate = new Date();
            this.startDate = {
              year: currentDate.getFullYear(),
              month: currentDate.getMonth() + 1,
              day: currentDate.getDate()
            };
            this.startTime = {hour: 0, minute: 0, second: 0};
          }
        }
      );
  }

  ngOnDestroy(): void {
    this.routeListener$.unsubscribe();
  }

  doSaveFleet() {
    if (this.validateInput()) {
      this.fleet.start = JSON.stringify({date: this.startDate, time: this.startTime});
      this.fleetService.saveFleet(this.fleet).subscribe(
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
      this.editFleet = false;
    }
  }

  open(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      if (result === 'Delete') {
        this.doDeleteFleet();
      }
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  validateInput() {
    return this.fleet.name && this.startDate && this.startTime;
  }

  editable() {
    return !(this.fleet && this.fleet.id) || this.editFleet;
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
}