import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UserService} from './user.service';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';

const INVITES_URI = 'http://' + environment.apiHost + ':8282/fleets/invites';

@Injectable({
  providedIn: 'root'
})
export class InviteService {

  constructor(private http: HttpClient, private userService: UserService) {
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
}
