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
import {CharacterService} from '../service/character.service';

@Component({
  selector: 'app-invitation-details',
  templateUrl: './invitation-details.component.html',
  styleUrls: ['./invitation-details.component.css']
})
export class InvitationDetailsComponent implements OnInit, OnDestroy {

  constructor(
      private inviteService: InviteService,
      private registrationService: RegistrationService,
      private userService: UserService,
      private characterService: CharacterService,
      private route: ActivatedRoute,
      private router: Router,
      private localStorageService: LocalStorageService) {
  }

  private routeListener$: Subscription;

  public errorMessage: string;
  public invite: InviteModel;
  public user: User;
  public start: any;
  public accepted: boolean;
  public fleetOwner: any;

  static prefixZero(digit: number) {
    if (digit < 10) {
      return '0' + digit;
    } else {
      return '' + digit
    }
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
                  this.characterService.getCharacter(this.invite.fleet.owner).subscribe(character => {
                    this.fleetOwner = character;
                  })
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
    return InvitationDetailsComponent.prefixZero(this.start.date.day)
        + '-'
        + InvitationDetailsComponent.prefixZero(this.start.date.month)
        + '-'
        + InvitationDetailsComponent.prefixZero(this.start.date.year);
  }

  displayTime() {
    return InvitationDetailsComponent.prefixZero(this.start.time.hour)
        + ':'
        + InvitationDetailsComponent.prefixZero(this.start.time.minute)
  }

  accept() {
    this.registrationService.registerForFleet(this.invite.fleet.id).subscribe(
      (registrationData: RegistrationModel) => {
        this.router.navigateByUrl('/fleets/registration/' + registrationData.fleetId + '/' + registrationData.characterId);
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
