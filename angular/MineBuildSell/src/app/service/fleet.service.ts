import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {UserService} from './user.service';
import {Observable} from 'rxjs';
import {FleetModel} from '../shared/model/fleet-model';
import {map} from 'rxjs/operators';

const FLEETS_URI = 'http://' + environment.apiHost + ':8282/fleets';

@Injectable({
  providedIn: 'root'
})
export class FleetService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  fleets: FleetModel[];

    getFleets(owned: boolean, active: boolean): Observable<FleetModel[]> {
      const httpOptions = {
        headers: new HttpHeaders({
          Authorization: 'Bearer ' + this.userService.getEveState()
        })
      };
      return this.http.get<any>(FLEETS_URI + '/?owned=' + owned + '&active=' + active, httpOptions)
        .pipe(
          map(result => {
            if (result.responseCode >= 400) {
              throw new Error('broken API:' + result.responseCode);
            } else {
              this.fleets = result.fleetList;
              return result.fleetList;
            }
          })
        );
  }

  saveFleet(fleet: FleetModel) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    if (fleet.id) { // update fleet
      return this.http.put<any>(FLEETS_URI + '/' + fleet.id, fleet, httpOptions)
        .pipe(
          map(result => {
            if (result.responseCode >= 400) {
              throw new Error('broken API:' + result.responseCode);
            } else {
              return fleet;
            }
          })
        );
    } else { // create fleet
      return this.http.post<any>(FLEETS_URI + '/', fleet, httpOptions)
        .pipe(
          map(result => {
            if (result.responseCode >= 400) {
              throw new Error('broken API:' + result.responseCode);
            } else {
              return result.fleet;
            }
          })
        );
    }
  }

  getFleet(id: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(FLEETS_URI + '/' + id, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.fleet;
          }
        })
      );
  }

  deleteFleet(fleet: FleetModel) {
    console.log('deleting', fleet);
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState(),
        'Content-Type': 'application/json'
      })
    };
    return this.http.delete<any>(FLEETS_URI + '/' + fleet.id, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode !== 410) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.responseCode;
          }
        })
      );
  }
}
