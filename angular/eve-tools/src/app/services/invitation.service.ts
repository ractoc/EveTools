import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UserService} from './user.service';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';
import {Invitation} from "./model/invitation";

const INVITES_URI = 'http://' + environment.apiHost + ':8282/fleets/invites';

@Injectable({
  providedIn: 'root'
})
export class InvitationService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  addInvitationToFleet(id: number, type: string, fleetId: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.post<any>(INVITES_URI + '/' + fleetId, {id, type}, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.invitationList;
          }
        })
      );
  }
}
