import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

import {UserService} from './user.service';
import {BlueprintModel} from '../shared/model/blueprint.model';
import {MarketHubModel} from '../shared/model/markethub.model';

import {environment} from '../../environments/environment';
import {ItemModel} from '../shared/model/item.model';

const CALCULATOR_URI = 'http://' + environment.apiHost + ':8888/calculator';

@Injectable({
  providedIn: 'root'
})
export class CalculatorService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  private static createUrl(type: string, buyMarketHub: MarketHubModel, sellMarketHub: MarketHubModel, nrRuns: number) {
    return CALCULATOR_URI + '/' +
      type + '/' +
      buyMarketHub.regionId + '/' +
      buyMarketHub.solarSystemId + '/' +
      sellMarketHub.regionId + '/' +
      sellMarketHub.solarSystemId +
      '?runs=' + nrRuns;
  }

  calculateBlueprint(blueprint: BlueprintModel,
                     buyMarketHub: MarketHubModel,
                     sellMarketHub: MarketHubModel,
                     nrRuns: number): Observable<BlueprintModel> {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };

    return this.http.post<any>(CalculatorService.createUrl('blueprint', buyMarketHub, sellMarketHub, nrRuns),
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

  calculateItem(item: ItemModel, buyMarketHub: MarketHubModel, sellMarketHub: MarketHubModel, nrRuns: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };

    return this.http.post<any>(CalculatorService.createUrl('item', buyMarketHub, sellMarketHub, nrRuns),
      item, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.item;
          }
        })
      );

  }
}
