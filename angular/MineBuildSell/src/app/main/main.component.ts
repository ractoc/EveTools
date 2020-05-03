import {Component, OnInit} from '@angular/core';
import {Subject} from 'rxjs';
import {Router} from '@angular/router';
import {registerLocaleData} from '@angular/common';
import localeIs from '@angular/common/locales/is';

import {UserService} from '../service/user.service';
import {User} from '../shared/model/user.model';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  public user: User;
  private userMonitor: Subject<User>;

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit() {
    registerLocaleData(localeIs);
    this.userMonitor = this.userService.monitorUser();
    this.userMonitor
      .subscribe((us: User) => {
        this.user = us;
      });
  }

  logout() {
    this.userService.logout();
    this.router.navigate(['home']);
  }
}
