import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {BlueprintModel} from '../shared/model/blueprint.model';
import {UserService} from './user.service';

const ASSETS_URI = 'http://localhost:8787/assets';

@Injectable({
  providedIn: 'root'
})
export class AssetsService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  getBlueprints(): Observable<BlueprintModel[]> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Authorization': 'Bearer ' + this.userService.getEveState()
      })
    };
    console.log('headers: ' + httpOptions);
    return this.http.get<any>(ASSETS_URI + '/blueprint/character', httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.bluePrintList;
          }
        })
      );
  }
}
