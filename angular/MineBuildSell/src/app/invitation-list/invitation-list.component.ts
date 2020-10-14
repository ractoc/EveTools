import {Component, OnInit} from '@angular/core';
import {InviteModel} from '../shared/model/invite-model';
import {InviteService} from '../service/invite.service';
import {ActivatedRoute, Router} from '@angular/router';
import {LoginComponent} from '../login/login.component';
import {UserService} from '../service/user.service';
import {LocalStorageService} from '../service/local-storage.service';

@Component({
  selector: 'app-invitation-list',
  templateUrl: './invitation-list.component.html',
  styleUrls: ['./invitation-list.component.css']
})
export class InvitationListComponent implements OnInit {
  errorMessage: string;
  invites: InviteModel[];

  constructor(
    private route: ActivatedRoute,
    private inviteService: InviteService,
    private router: Router,
    private login: LoginComponent,
    private userService: UserService,
    private localStorageService: LocalStorageService) {
  }

  ngOnInit(): void {
    this.inviteService.getMyInvites().subscribe(
      (inviteData: InviteModel[]) => {
        if (inviteData && inviteData.length > 0) {
          this.invites = inviteData;
        } else {
          this.errorMessage = 'No invites to display';
        }
      },
      err => {
        this.errorMessage = err;
        if (err.status === 401) {
          this.localStorageService.remove('eve-state');
          this.localStorageService.set('currentPage', '/fleets/invites');
          this.router.navigateByUrl('/login');
        }
      }
    );
  }

  displayFleetStart(invite: InviteModel) {
    const start = JSON.parse(invite.fleet.start);
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

  // add a class to differentiate invites to past events, these should actually be filtered
  inviteClass(invite: InviteModel) {
    return '';
  }
}
