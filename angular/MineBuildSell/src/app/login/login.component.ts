import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {DOCUMENT} from '@angular/common';
import {UserService} from '../service/user.service';
import {environment} from '../../environments/environment';
import {User} from '../shared/model/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  private routeListener$: Subscription;

  public user: User;

  constructor(private userService: UserService,
              private route: ActivatedRoute,
              private router: Router,
              @Inject(DOCUMENT) private document: Document) {
  }

  ngOnInit() {
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
        if (params.eveState) {
          this.userService.getUser(params.eveState)
            .subscribe(
              user => {
                this.user = user;
                const currentPage = localStorage.getItem('currentPage');
                if (currentPage) {
                  localStorage.removeItem('currentPage');
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
