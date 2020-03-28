import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

import {Observable} from "rxjs";
import {map} from "rxjs/operators";

import {UserService} from "./user.service";
import {BlueprintModel} from "../shared/model/blueprint.model";
import {MarketHubModel} from "../shared/model/markethub.model";

import {environment} from "../../environments/environment";

const CALCULATOR_URI = 'http://' + environment.apiHost + ':8888/calculator';

@Injectable({
  providedIn: 'root'
})
export class CalculatorService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  calculateBlueprint(blueprint: BlueprintModel, buyMarketHub: MarketHubModel, sellMarketHub: MarketHubModel, nrRuns: number): Observable<BlueprintModel> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Authorization': 'Bearer ' + this.userService.getEveState()
      })
    };
    // TODO: add functionality to dynamically determine region, solarsystem and runs
    return this.http.post<any>(CALCULATOR_URI + "/blueprint/" + buyMarketHub.regionId + "/" + buyMarketHub.solarSystemId + "/" + sellMarketHub.regionId + "/" + sellMarketHub.solarSystemId + "?runs=" + nrRuns,
      blueprint, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.blueprint;
          }
        })
      );
  }
}
