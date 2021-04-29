import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {map} from "rxjs/operators";

import {environment} from "../../environments/environment";
import {UserService} from "./user.service";

const TYPES_URI = 'http://' + environment.apiHost + ':8282/fleets/types';

@Injectable({
  providedIn: 'root'
})
export class TypeService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  loadTypes() {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(TYPES_URI + '/', httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.types;
          }
        })
      );
  }
}
