import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UserService} from './user.service';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';

const REGISTRATIONS_URI = 'http://' + environment.apiHost + ':8282/fleets/registrations';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  loadRegistrationsForFleet(fleetId: number) {
    console.log('loading registrations for fleet', fleetId);
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(REGISTRATIONS_URI + '/fleet/' + fleetId, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.registrations?result.registrations:[];
          }
        })
      );
  }

  removeRegistration(fleetId: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.delete<any>(REGISTRATIONS_URI + '/' + fleetId, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode !== 410) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            console.log('remaining registrations', result.registrations)
            return result.registrations?result.registrations:[];
          }
        })
      );
  }
}
