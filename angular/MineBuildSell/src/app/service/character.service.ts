import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {BlueprintModel} from '../shared/model/blueprint.model';
import {UserService} from './user.service';

const CHARACTER_URI = 'http://localhost:8686/character';

@Injectable({
  providedIn: 'root'
})
export class CharacterService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  getBlueprints(): Observable<BlueprintModel[]> {
    return this.http.get<any>(CHARACTER_URI + '/characters/blueprints',
      {
        headers: {
          Authorization: 'Bearer ' + this.userService.getEveState()
        }
      }).pipe(map(result => {
      if (result.responseCode >= 400) {
        throw new Error('broken API:' + result.responseCode);
      } else {
        return result.bluePrintList;
      }
    }));
  }
}
