import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {Subject} from "rxjs";
import {Router} from "@angular/router";
import {User} from "../../services/model/user";
import {LocalStorageService} from "../../services/local-storage.service";
import {UserService} from "../../services/user.service";
import {registerLocaleData} from "@angular/common";
import localeIs from "@angular/common/locales/is";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  @Output() public sidenavToggle = new EventEmitter();
  user: any;
  private userMonitor: Subject<User>;

  constructor(private userService: UserService,
              private localStorageService: LocalStorageService,
              private router: Router) { }

  ngOnInit(): void {
    registerLocaleData(localeIs);
    this.userMonitor = this.userService.monitorUser();
    this.userMonitor
      .subscribe((us: User) => {
        this.user = us;
      });
  }

  logout() {
    this.userService.logout().subscribe(
      () => {
        this.localStorageService.remove('eve-state');
        this.router.navigate(['home']);
      },
      err => {
        if (err.status === 401) {
          this.localStorageService.remove('eve-state');
          this.router.navigate(['home']);
        }
      });
  }

  switch() {
    this.userService.switch().subscribe(
      () => {
        this.localStorageService.remove('eve-state');
        this.router.navigateByUrl('/user/login');
      },
      err => {
        if (err.status === 401) {
          this.localStorageService.remove('eve-state');
          this.router.navigateByUrl('/user/login');
        }
      });
  }

  onToggleSidenav() {
    this.sidenavToggle.emit();
  }
}
