import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {DOCUMENT} from '@angular/common';
import {UserService} from '../service/user.service';
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  private routeListener$: Subscription;

  public userName: string;

  constructor(private userService: UserService,
              private route: ActivatedRoute,
              private router: Router,
              @Inject(DOCUMENT) private document: Document) {
  }

  ngOnInit() {
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
        if (params.eveState) {
          this.userService.getUsername(params.eveState)
            .subscribe(
              un => {
                this.userName = un.name;
                this.router.navigate(['home']);
              });
        } else {
          this.document.location.href = 'http://' + environment.apiHost + ':8484/user/launchSignOn';
        }
      });
  }

  ngOnDestroy(): void {
    this.routeListener$.unsubscribe();
  }

}
