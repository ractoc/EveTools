import {Component, OnInit} from '@angular/core';
import {Subject} from 'rxjs';
import {Router} from '@angular/router';

import {UserService} from '../service/user.service';
import {User} from '../shared/model/user.model';
import {LocalStorageService} from '../service/local-storage.service';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  public user: User;
  private userMonitor: Subject<User>;

  constructor(private userService: UserService,
              private localStorageService: LocalStorageService,
              private router: Router) {
  }

  ngOnInit() {
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

  switch() {
    this.userService.switch().subscribe(
      () => {
        this.localStorageService.remove('eve-state');
        this.router.navigateByUrl('/login');
      },
      err => {
        if (err.status === 401) {
          this.localStorageService.remove('eve-state');
          this.router.navigateByUrl('/login');
        }
      });
  }
}
