import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import {catchError, map} from 'rxjs/operators';
import {BehaviorSubject, Observable, of} from 'rxjs';

import {User} from '../shared/model/user.model';
import {environment} from '../../environments/environment';

const USER_URI = 'http://' + environment.apiHost + ':8484/user/api/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user$: BehaviorSubject<User> = new BehaviorSubject<User>(null);
  private observable: Observable<User>;
  private eveState: string;

  constructor(private http: HttpClient) {
  }

  getUser(eveState: string): Observable<User> {
    this.eveState = eveState;
    if (this.observable) {
      return this.observable;
    } else {
      this.observable = this.http.get<any>(USER_URI + '/' + eveState)
        .pipe(map(result => {
            this.observable = null;
            const user = new User(result.eveState, result.name, result.roles);
            this.user$.next(user);
            return user;
          }),
          catchError((error: any) => {
            console.log('Error while fetching cities: ', error);
            return of(null);
          }));
      return this.observable;
    }
  }

  monitorUser() {
    return this.user$;
  }

  logout() {
    this.user$.next(null);
  }

  getEveState() {
    return this.eveState;
  }
}
