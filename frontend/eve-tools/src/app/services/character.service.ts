import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";

const EVE_CHARACTER_URI = 'https://esi.evetech.net/latest/characters';

@Injectable({
  providedIn: 'root'
})
export class CharacterService {

  constructor(private http: HttpClient) { }

  getCharacter(characterId: number) {
    return this.http.get<any>(
      EVE_CHARACTER_URI +
      '/' + characterId
    ).pipe(
      map(result => {
        if (result.responseCode >= 400) {
          throw new Error('broken API:' + result.responseCode);
        } else {
          return {
            id: characterId,
            name: result.name,
            type: 'character',
            corporationId: result.corporation_id,
            portrait: undefined
          }
        }
      })
    );
  }

  getPortrait(characterId: number) {
    return this.http.get<any>(
      EVE_CHARACTER_URI +
      '/' + characterId +
      '/portrait'
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
