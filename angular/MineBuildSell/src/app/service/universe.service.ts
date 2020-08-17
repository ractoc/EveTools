import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

import {Observable} from "rxjs";
import {map} from "rxjs/operators";

import {UserService} from "./user.service";
import {MarketHubModel} from "../shared/model/markethub.model";

import {environment} from "../../environments/environment";

const UNIVERSE_URI = 'http://' + environment.apiHost + ':8585/universe';

@Injectable({
  providedIn: 'root'
})
export class UniverseService {

  private marketHubs: MarketHubModel[];

  constructor(private http: HttpClient, private userService: UserService) {
  }

  getMarketHubs(): Observable<MarketHubModel[]> {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(UNIVERSE_URI + '/marketHubs/', httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            this.marketHubs = result.marketHubList;
            return result.marketHubList;
          }
        })
      );
  }
}
