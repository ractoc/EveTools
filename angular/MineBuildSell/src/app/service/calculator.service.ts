import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {UserService} from "./user.service";
import {BlueprintModel} from "../shared/model/blueprint.model";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

const CALCULATOR_URI = 'http://localhost:8888/calculator';

@Injectable({
  providedIn: 'root'
})
export class CalculatorService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  calculateBlueprint(blueprint: BlueprintModel): Observable<BlueprintModel> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Authorization': 'Bearer ' + this.userService.getEveState()
      })
    };
    // TODO: add functionality to dynamically determine region, solarsystem and runs
    return this.http.post<any>(CALCULATOR_URI + "/blueprint/10000043/30002187?runs=100",
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
