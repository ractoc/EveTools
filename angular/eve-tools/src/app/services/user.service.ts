import {Inject, Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of} from "rxjs";
import {HTTP_INTERCEPTORS, HttpClient, HttpHeaders} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";

import {User} from "./model/user";
import {environment} from '../../environments/environment';

const USER_URI = 'http://' + environment.apiHost + ':8484/user/api';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user$: BehaviorSubject<User> = new BehaviorSubject<User>(null);
  private observable: Observable<User>;
  private eveState: string;

  private currentUser: User;

  constructor(private http: HttpClient) {
  }

  getUser(eveState: string): Observable<any> {
    this.eveState = eveState;
    if (this.observable) {
      return this.observable;
    } else {
      this.observable = this.http.get<any>(USER_URI + '/userdetails/' + eveState)
        .pipe(map(user => {
            this.observable = null;
            this.currentUser = new User(
              user.eveState,
              user.characterName,
              user.roles,
              user.charId,
              user.accessToken
            );
            this.user$.next(this.currentUser);
            return this.currentUser;
          }),
          catchError(() => {
            return of(null);
          }));
      return this.observable;
    }
  }

  getCurrentUser(): User {
    return this.currentUser;
  }

  monitorUser() {
    return this.user$;
  }

  switch() {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.getEveState(),
        'Content-Type': 'application/json'
      })
    };
    return this.http.delete<any>(USER_URI + '/user', httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode !== 410) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.responseCode;
          }
        })
      );
  }

  logout() {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.getEveState(),
        'Content-Type': 'application/json'
      })
    };
    return this.http.delete<any>(USER_URI + '/user', httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode !== 410) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            this.user$.next(null);
            this.currentUser = undefined;
            this.eveState = undefined;
            return result.responseCode;
          }
        })
      );
  }

  getEveState() {
    return this.eveState;
  }
}
