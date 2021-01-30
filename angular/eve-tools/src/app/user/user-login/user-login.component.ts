import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {DOCUMENT} from "@angular/common";
import {UserService} from "../../services/user.service";
import {User} from "../../services/model/user";
import {LocalStorageService} from "../../services/local-storage.service";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent implements OnInit, OnDestroy {

  private routeListener$: Subscription;

  user: User;

  constructor(private userService: UserService,
              private route: ActivatedRoute,
              private router: Router,
              private localStorageService: LocalStorageService,
              @Inject(DOCUMENT) private document: Document) {
  }

  ngOnInit() {
    console.log('params:', this.route.params);

    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
        const eveState = params.eveState ? params.eveState : this.localStorageService.get('eve-state');
        if (eveState) {
          this.userService.getUser(eveState)
            .subscribe(
              result => {
                if (result === undefined || result == null) {
                  this.document.location.href = 'http://' + environment.apiHost + ':8484/user/launchSignOn';
                }
                this.user = result;
                const currentPage = this.localStorageService.get('currentPage');
                this.localStorageService.set('eve-state', eveState);
                if (currentPage) {
                  this.localStorageService.remove('currentPage');
                  this.router.navigateByUrl(currentPage);
                } else {
                  this.router.navigate(['home']);
                }
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
