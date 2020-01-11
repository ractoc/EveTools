import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {DOCUMENT} from "@angular/common";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  private routeListener$: Subscription;

  constructor(private route: ActivatedRoute, private router: Router, @Inject(DOCUMENT) private document: Document) {
  }

  ngOnInit() {
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
        console.log("params", params);
        if (params.eveState) {
          console.log("eveState: " + params.eveState);
          this.router.navigate(['/home']);
        } else {
          console.log("no eve-state, redirecting to signon");
          this.document.location.href = "http://localhost:8484/user/launchSignOn";
        }
      });
  }

  ngOnDestroy(): void {
    this.routeListener$.unsubscribe();
  }

}
