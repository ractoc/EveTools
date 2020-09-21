import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {UserService} from './user.service';
import {environment} from '../../environments/environment';

const REGISTRATIONS_URI = 'http://' + environment.apiHost + ':8282/fleets/registrations';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  registerForFleet(fleetId: number) {
    return this.sendConfirmationForFleet(fleetId, true);
  }

  declineForFleet(fleetId: number) {
    return this.sendConfirmationForFleet(fleetId, false);
  }

  private sendConfirmationForFleet(fleetId: number, choice: boolean) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.post<any>(REGISTRATIONS_URI + '/' + fleetId, {accept: choice}, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.registration;
          }
        })
      );
  }
}
