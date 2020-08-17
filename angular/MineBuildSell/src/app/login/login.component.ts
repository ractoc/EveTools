import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {DOCUMENT} from '@angular/common';
import {UserService} from '../service/user.service';
import {environment} from '../../environments/environment';
import {User} from '../shared/model/user.model';
import {LocalStorageService} from '../service/local-storage.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  private routeListener$: Subscription;

  user: User;

  constructor(private userService: UserService,
              private route: ActivatedRoute,
              private router: Router,
              private localStorageService: LocalStorageService,
              @Inject(DOCUMENT) private document: Document) {
  }

  ngOnInit() {
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
