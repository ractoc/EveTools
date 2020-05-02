import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';

import {BlueprintModel} from '../shared/model/blueprint.model';
import {UserService} from './user.service';

import {environment} from '../../environments/environment';
import {MarketGroupModel} from '../shared/model/marketgroup.model';
import {ItemModel} from '../shared/model/item.model';

const ASSETS_URI = 'http://' + environment.apiHost + ':8787/assets';

@Injectable({
  providedIn: 'root'
})
export class AssetsService {

  constructor(private http: HttpClient, private userService: UserService) {
  }

  blueprints: BlueprintModel[];
  marketGroupParents = new Map<number, MarketGroupModel[]>();

  getPersonalBlueprints(): Observable<BlueprintModel[]> {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(ASSETS_URI + '/blueprint/character', httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            this.blueprints = result.blueprintList;
            return result.blueprintList;
          }
        })
      );
  }

  getCorporateBlueprints(): Observable<BlueprintModel[]> {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return this.http.get<any>(ASSETS_URI + '/blueprint/corporation', httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            this.blueprints = result.blueprintList;
            return result.blueprintList;
          }
        })
      );
  }

  getBlueprint(blueprintID: number, blueprintType: string): Observable<BlueprintModel> {
    return new Observable<BlueprintModel>((observe) => {
      if (blueprintType === 'personal' && this.blueprints === undefined) {
        this.getPersonalBlueprints().subscribe(
          (blueprintData: BlueprintModel[]) => {
            if (blueprintData && blueprintData.length > 0) {
              this.blueprints = blueprintData;
              observe.next(blueprintData.find(bp => +bp.id === +blueprintID));
            }
          },
          err => {
            observe.error(err);
          }
        );
      } else if (blueprintType === 'corporate' && this.blueprints === undefined) {
        this.getCorporateBlueprints().subscribe(
          (blueprintData: BlueprintModel[]) => {
            if (blueprintData && blueprintData.length > 0) {
              this.blueprints = blueprintData;
              observe.next(this.blueprints.find(bp => +bp.id === +blueprintID));
            }
          },
          err => {
            observe.error(err);
          }
        );
      } else if (this.blueprints !== undefined) {
        // tslint:disable-next-line:triple-equals
        const bpFound = this.blueprints.find(bp => +bp.id === +blueprintID);
        observe.next(bpFound);
      }
    });
  }

  getMarketGroups(parentGroupId: number): Observable<MarketGroupModel[]> {
    return new Observable<MarketGroupModel[]>((observe) => {
      if (this.marketGroupParents.get(parentGroupId)) {
        observe.next(this.marketGroupParents.get(parentGroupId));
      } else {
        const httpOptions = {
          headers: new HttpHeaders({
            Authorization: 'Bearer ' + this.userService.getEveState()
          })
        };
        this.http.get<any>(ASSETS_URI + '/group/' + parentGroupId, httpOptions)
          .pipe(
            map(result => {
              if (result.responseCode >= 400) {
                throw new Error('broken API:' + result.responseCode);
              } else {
                return result.marketGroupList;
              }
            })
          ).subscribe(groups => {
          if (!groups) {
            observe.next(new Array<MarketGroupModel>());
          } else {
            groups.forEach(group => {
              if (!this.marketGroupParents.get(group.parentId)) {
                this.marketGroupParents.set(group.parentId, new Array<MarketGroupModel>());
              }
              this.marketGroupParents.get(group.parentId).push(group);
            });
            observe.next(groups);
          }
        });
      }
    });
  }

  getItemsForMarketGroup(marketGroup: MarketGroupModel): Observable<ItemModel[]> {
    return new Observable<ItemModel[]>((observe) => {
      const httpOptions = {
        headers: new HttpHeaders({
          Authorization: 'Bearer ' + this.userService.getEveState()
        })
      };
      this.http.get<any>(ASSETS_URI + '/item/?group=' + marketGroup.id, httpOptions)
        .pipe(
          map(result => {
            if (result.responseCode >= 400) {
              throw new Error('broken API:' + result.responseCode);
            } else {
              return result.itemList;
            }
          })
        ).subscribe(items => {
        observe.next(items);
      });
    });
  }
}
