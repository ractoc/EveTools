import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {UserService} from "./user.service";
import {map} from "rxjs/operators";

const ROLES_URI = 'http://' + environment.apiHost + ':8282/fleets/roles';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  constructor(private http: HttpClient, private userService: UserService) { }

  loadRoles(typeId: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(ROLES_URI + '/' + typeId, httpOptions)
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
