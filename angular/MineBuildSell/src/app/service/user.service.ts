import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, map} from 'rxjs/operators';

import {User} from "../shared/model/user.model";
import {Observable, of, BehaviorSubject} from "rxjs";

const USER_URI = "http://localhost:8484/user/api/username";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private username$: BehaviorSubject<string>  = new BehaviorSubject<string>(null);
  private observable: Observable<User>;
  private eveState: string;

  constructor(private http: HttpClient) {
  }

  getUsername(eveState: string): Observable<User> {
    this.eveState = eveState;
    if (this.observable) {
      return this.observable;
    } else {
      this.observable = this.http.get<any>(USER_URI + '/' + eveState)
        .pipe(map(result => {
            this.observable = null;
            const user = new User(result.eveState, result.characterName);
            this.username$.next(user.name);
            return user;
          }),
          catchError((error: any) => {
            console.log('Error while fetching cities: ', error);
            return of(null);
          }));
      return this.observable;
    }
  }

  monitorUsername() {
    return this.username$;
  }

  logout() {
    this.username$.next(null);
  }

  getEveState() {
    return this.eveState;
  }
}
