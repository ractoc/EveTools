import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {LocalStorageService} from '../service/local-storage.service';
import {InviteModel} from '../shared/model/invite-model';
import {InviteService} from '../service/invite.service';
import {User} from '../shared/model/user.model';
import {UserService} from '../service/user.service';
import {RegistrationService} from '../service/registration.service';
import {RegistrationModel} from '../shared/model/Registration-model';

@Component({
  selector: 'app-invitation-details',
  templateUrl: './invitation-details.component.html',
  styleUrls: ['./invitation-details.component.css']
})
export class InvitationDetailsComponent implements OnInit, OnDestroy {
  private routeListener$: Subscription;

  public errorMessage: string;
  public invite: InviteModel;
  public user: User;
  public start: any;
  public accepted: boolean;

  constructor(
    private inviteService: InviteService,
    private registrationService: RegistrationService,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private localStorageService: LocalStorageService) {
  }

  ngOnInit(): void {
    this.errorMessage = undefined;
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
          if (params.key) {
            this.inviteService.getInvite(params.key).subscribe(
              (inviteData: InviteModel) => {
                this.invite = inviteData;
                this.start = JSON.parse(inviteData.fleet.start)
                this.user = this.userService.getCurrentUser();
              },
              err => {
                console.log('error opening invite details')
                if (err.status === 401) {
                  console.log('redirecting to login');
                  this.localStorageService.set('currentPage', '/fleets/invite/' + params.key);
                  this.router.navigateByUrl('/login');
                } else {
                  this.errorMessage = 'Unable to load invite';
                }
              }
            );
          }
        }
      );
  }

  ngOnDestroy(): void {
    this.routeListener$.unsubscribe();
  }

  displayDate() {
    return this.prefixZero(this.start.date.day)
      + '-'
      + this.prefixZero(this.start.date.month)
      + '-'
      + this.prefixZero(this.start.date.year);
  }

  displayTime() {
    return this.prefixZero(this.start.time.hour)
      + ':'
      + this.prefixZero(this.start.time.minute)
  }

  private prefixZero(digit: number) {
    if (digit < 10) {
      return '0' + digit;
    } else {
      return '' + digit
    }
  }

  accept() {
    this.registrationService.registerForFleet(this.invite.fleet.id).subscribe(
      (registrationData: RegistrationModel) => {
        this.router.navigateByUrl('/fleets/registration/' + registrationData.fleetId);
      }
    );
  }

  decline() {
    this.registrationService.declineForFleet(this.invite.fleet.id).subscribe(
      (registrationData: RegistrationModel) => {
        this.router.navigateByUrl('/fleets');
      }
    );
  }
}
