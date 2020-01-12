import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, map} from 'rxjs/operators';

import {User} from "../shared/model/user.model";
import {Observable, of} from "rxjs";

const USER_URI = "http://localhost:8484/user/api/username";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user: User;
  private observable: Observable<User>;

  constructor(private http: HttpClient) {
  }

  getUsername(eveState: string): Observable<User> {
    if (this.user) {
      return of(this.user);
    } else if (this.observable) {
      return this.observable;
    } else {
      this.observable = this.http.get<any>(USER_URI + '/' + eveState)
        .pipe(map(result => {
            console.log("result", result);
            this.observable = null;
            this.user = new User(result.eveState, result.characterName);
            return this.user;
          }),
          catchError((error: any) => {
            //  3c. Error handling here, omdat we nu async-pipe gebruiken.
            console.log('Error while fetching cities: ', error);
            return of(null);
          }));
      return this.observable;
    }
  }
}
