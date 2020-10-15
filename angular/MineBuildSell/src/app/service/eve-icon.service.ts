import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

const EVE_CORPORATION_ICON_URI = 'https://esi.evetech.net/latest/corporations'
const EVE_CHARACTER_ICON_URI = 'https://esi.evetech.net/latest/characters'

@Injectable({
  providedIn: 'root'
})
export class EveIconService {

  constructor(private http: HttpClient) {
  }

  getCorporationIcon(corporationId: number) {
    return this.http.get<any>(
      EVE_CORPORATION_ICON_URI + '/' +
      corporationId + '/icons'
    );
  }

  getCharacterIcon(characterId: number) {
    return this.http.get<any>(
      EVE_CHARACTER_ICON_URI + '/' +
      characterId + '/portrait'
    );
  }
}
