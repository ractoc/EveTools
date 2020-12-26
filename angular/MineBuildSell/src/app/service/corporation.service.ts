import {Injectable} from '@angular/core';
import {map} from "rxjs/operators";
import {HttpClient} from "@angular/common/http";

const EVE_CORPORATION_URI = 'https://esi.evetech.net/latest/corporations';

@Injectable({
  providedIn: 'root'
})
export class CorporationService {

  constructor(private http: HttpClient) {
  }

  getCorporation(corporationId) {
    return this.http.get<any>(
      EVE_CORPORATION_URI +
      '/' + corporationId
    ).pipe(
      map(result => {
        if (result.responseCode >= 400) {
          throw new Error('broken API:' + result.responseCode);
        } else {
          return {
            id: corporationId,
            name: result.name,
            type: 'corporation',
            portrait: undefined
          }
        }
      })
    );
  }

  getIcon(corporationId: number) {
    return this.http.get<any>(
      EVE_CORPORATION_URI + '/' +
      corporationId + '/icons'
    ).pipe(
      map(result => {
        if (result.responseCode >= 400) {
          throw new Error('broken API:' + result.responseCode);
        } else {
          return result.px64x64;
        }
      })
    );
  }
}
