import {Component, OnInit} from '@angular/core';
import {Subject} from 'rxjs';
import {UserService} from '../service/user.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  private userName: string;
  private usernameMonitor: Subject<string>;

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit() {
    this.usernameMonitor = this.userService.monitorUsername();
    this.usernameMonitor
      .subscribe((un: string) => {
        this.userName = un;
      });
  }

  logout() {
    this.userService.logout();
    this.router.navigate(['home']);
  }
}