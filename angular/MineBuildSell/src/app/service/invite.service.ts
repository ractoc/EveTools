import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UserService} from './user.service';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';
import {FleetModel} from "../shared/model/fleet-model";

const INVITES_URI = 'http://' + environment.apiHost + ':8282/fleets/invites';

@Injectable({
  providedIn: 'root'
})
export class InviteService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  getMyInvites() {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(INVITES_URI + '/', httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.invites;
          }
        })
      );
  }

  getFleetInvites(fleet: FleetModel) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(INVITES_URI + '/fleet/' + fleet.id, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.invites;
          }
        })
      );
  }

  getInvite(key: string) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(INVITES_URI + '/' + key, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.invite;
          }
        })
      );
  }

  addInvitation(fleetId: number, type: string, id: number, name: string) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    console.log('adding invite', {type, id, fleetId});
    return this.http.post<any>(INVITES_URI + '/', {type, id, fleetId, name}, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.inviteKey;
          }
        })
      );
  }

  declineInvite(key: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(INVITES_URI + '/decline/' + key, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.invite;
          }
        })
      );
  }

  deleteInvitation(key: string) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.delete<any>(INVITES_URI + '/' + key, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode !== 410) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.invite;
          }
        })
      );
  }
}
