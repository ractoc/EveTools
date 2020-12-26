import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';

const EVE_SEARCH_URI = 'https://esi.evetech.net/latest/search';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(private http: HttpClient) {
  }

  searchCharacters(searchString: string) {
    return this.http.get<any>(
      EVE_SEARCH_URI +
      '?categories=character' +
      '&search=' + searchString
    ).pipe(
      map(result => {
        if (result.responseCode >= 400) {
          throw new Error('broken API:' + result.responseCode);
        } else {
          return result.character;
        }
      })
    );
  }
}
