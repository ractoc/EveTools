import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";

const EVE_SEARCH_URI = 'https://esi.evetech.net/latest/search';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(private http: HttpClient) { }

  search(searchString: string, type: string) {
    return this.http.get<any>(
      EVE_SEARCH_URI +
      '?categories=' + type +
      '&search=' + searchString
    ).pipe(
      map(result => {
        if (result.responseCode >= 400) {
          throw new Error('broken API:' + result.responseCode);
        } else {
          if (type === 'character') {
            return result.character;
          } else if (type === 'corporation') {
            return result.corporation;
          }
        }
      })
    );
  }
}
