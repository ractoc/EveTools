import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";

const UNIVERSE_URI = 'https://esi.evetech.net/latest/universe';

@Injectable({
  providedIn: 'root'
})
export class UniverseService {

  constructor(private http: HttpClient) {
  }

  getSolarSystem(id: number) {
    return this.http.get<any>(UNIVERSE_URI + '/systems/' + id)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            console.log('solarsystem: ', result);
            return {
              id: result.system_id,
              name: result.name,
              type: 'solar-system',
              portrait: undefined
            };
          }
        })
      );
  }
}
