import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {UserService} from './user.service';
import {environment} from '../../environments/environment';
import {FleetModel} from '../shared/model/fleet-model';
import {RegistrationModel} from "../shared/model/Registration-model";

const REGISTRATIONS_URI = 'http://' + environment.apiHost + ':8282/fleets/registrations';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  getFleetRegistrations(fleet: FleetModel) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(REGISTRATIONS_URI + '/fleet/' + fleet.id, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.registrations;
          }
        })
      );
  }

  getFleetRegistrationUser(fleetId: number, charId: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(
      REGISTRATIONS_URI + '/fleet/' + fleetId + '/character/' + charId,
      httpOptions)
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

  registerForFleet(fleetId: number) {
    return this.sendConfirmationForFleet(fleetId, true);
  }

  declineForFleet(fleetId: number) {
    return this.sendConfirmationForFleet(fleetId, false);
  }

  updateRegistration(registration: RegistrationModel) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.put<any>(REGISTRATIONS_URI + '/' + registration.fleetId, registration, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return registration;
          }
        })
      );
  }

  deleteRegistration(id: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.delete<any>(REGISTRATIONS_URI + '/' + id, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode !== 410) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.registration;
          }
        })
      );
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
