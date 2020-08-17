import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import {catchError, map} from 'rxjs/operators';
import {BehaviorSubject, Observable, of} from 'rxjs';

import {User} from '../shared/model/user.model';
import {environment} from '../../environments/environment';

const USER_URI = 'http://' + environment.apiHost + ':8484/user/api/userdetails';

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
      this.observable = this.http.get<any>(USER_URI + '/' + eveState)
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
          catchError((error: any) => {
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

  logout() {
    this.user$.next(null);
    this.currentUser = undefined;
  }

  getEveState() {
    return this.eveState;
  }
}
