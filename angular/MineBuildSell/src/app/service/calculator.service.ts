import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {map} from 'rxjs/operators';

import {UserService} from './user.service';
import {MarketHubModel} from '../shared/model/markethub.model';

import {environment} from '../../environments/environment';
import {ItemModel} from '../shared/model/item.model';
import {BlueprintModel} from '../shared/model/blueprint.model';

const ASSETS_URI = 'http://' + environment.apiHost + ':8787/assets'
const EVE_MARKET_URI = 'https://esi.evetech.net/latest/markets'
const EVE_CHARACTER_URI = 'https://esi.evetech.net/latest/characters';
const EVE_UNIVERSE_URI = 'https://esi.evetech.net/latest/universe';
const EVE_INDUSTRY_URI = 'https://esi.evetech.net/latest/industry'


const SKILL_ACCOUNTING = 16622;
const SKILL_BROKER_RELATIONS = 3446;

@Injectable({
  providedIn: 'root'
})
export class CalculatorService {
  private stationId: number;

  constructor(private http: HttpClient, private userService: UserService) {
  }

  async calculateBlueprint(blueprint: BlueprintModel,
                           buyMarketHub: MarketHubModel,
                           sellMarketHub: MarketHubModel,
                           nrRuns: number): Promise<BlueprintModel> {
    const bp: BlueprintModel = await this.getBlueprint(blueprint.id).then(result => {
      return result;
    });
    bp.locationId = blueprint.locationId;

    await this.calculateMaterialPrices(bp, buyMarketHub, sellMarketHub, nrRuns);

    await this.getBuyPrice(bp.id, buyMarketHub).then(order => {
      bp.buyPrice = order.price;
    });

    const item: ItemModel = await this.getItemForBlueprint(bp.id).then(result => {
      return result;
    });
    bp.item = await this.calculateItem(item, buyMarketHub, sellMarketHub, nrRuns).then(result => {
      return result;
    });

    bp.jobInstallationCosts = await this.calculateJobInstallationCosts(bp).then(result => {
      return result;
    });

    return bp;
  }

  async calculateItem(item: ItemModel, buyMarketHub: MarketHubModel, sellMarketHub: MarketHubModel, nrRuns: number) {
    this.stationId = undefined;

    await this.getBuyPrice(item.id, buyMarketHub).then(order => {
      item.buyPrice = order.price * nrRuns;
    });
    await this.getSellPrice(item.id, sellMarketHub).then(order => {
      item.sellPrice = order.price * nrRuns;
      this.stationId = order.location_id;
    });

    item.salesTax = await this.calculateSalesTax(item,
      this.userService.getCurrentUser().characterId,
      this.userService.getCurrentUser().accessToken).then(tax => {
      return tax;
    });

    item.brokerFee = await this.calculateBrokerFee(item,
      this.userService.getCurrentUser().characterId,
      this.userService.getCurrentUser().accessToken).then(brokerFee => {
      return brokerFee;
    });

    return item;
  }

  private async getBlueprint(id: number): Promise<BlueprintModel> {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };

    return await this.http.get<any>(ASSETS_URI + '/blueprint/' + id, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.blueprint;
          }
        })
      ).toPromise();
  }

  private async getItemForBlueprint(id: number): Promise<ItemModel> {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userService.getEveState()
      })
    };
    return await this.http.get<any>(ASSETS_URI + '/item/blueprint/' + id, httpOptions)
      .pipe(
        map(result => {
          if (result.responseCode >= 400) {
            throw new Error('broken API:' + result.responseCode);
          } else {
            return result.item;
          }
        })
      ).toPromise();
  }

  private async calculateMaterialPrices(bp: BlueprintModel,
                                        buyMarketHub: MarketHubModel,
                                        sellMarketHub: MarketHubModel,
                                        nrRuns: number) {
    bp.manufacturingMaterials.forEach(mat => {
      mat.calculatedTotalQuantity = this.calculateActualQuantity(nrRuns, mat.quantity, bp.materialEfficiency);
      this.getBuyPrice(mat.typeId, buyMarketHub).then(result => {
        mat.buyPrice = result.price;
        bp.mineralBuyPrice += mat.buyPrice * mat.calculatedTotalQuantity;
      });
      this.getSellPrice(mat.typeId, sellMarketHub).then(result => {
        mat.sellPrice = result.price;
        bp.mineralSellPrice += mat.sellPrice * mat.calculatedTotalQuantity;
      });
    });
  }

  private calculateActualQuantity(nrRuns: number, baseQuantity: number, materialEfficiency: number) {
    const quantity = Math.ceil.apply(
      (
        Math.round(nrRuns * baseQuantity * this.calculateMaterialModifier(materialEfficiency))
      )
    );
    if (quantity && quantity > nrRuns) {
      return quantity;
    }
    return nrRuns;
  }

  private calculateMaterialModifier(materialEfficiency: number): number {
    return (100.00 - materialEfficiency) / 100.00 * (100.00 - 2) / 100.00;
  }

  private async calculateSalesTax(item: ItemModel, charId: number, accessToken: string): Promise<number> {
    const accountingSkillLevel: number = await this.getSkillLevel(SKILL_ACCOUNTING, charId, accessToken).then(level => {
      return level;
    });
    const tax: number = item.sellPrice * 0.05 * (1.0 - accountingSkillLevel * 0.11);
    console.log(item.sellPrice + '* 0.05 * (1.0 -  ' + accountingSkillLevel + ' * 0.11) = ' + tax);
    return Promise.resolve(tax);
  }

  private async calculateBrokerFee(item: ItemModel, charId: number, accessToken: string): Promise<number> {
    let percentage: number = await this.getStation(this.stationId).then(station => {
      return station.reprocessing_stations_take;
    }).catch(error => {
      return undefined;
    }).then(result => {
      return result;
    });

    if (!percentage) {
      percentage = await this.getStructure(this.stationId, accessToken).then(found => {
        return 0.01;
      }).then(result => {
        return result;
      });
    }
    const brokersRelationsSkillLevel: number = await this.getSkillLevel(SKILL_BROKER_RELATIONS, charId, accessToken).then(level => {
      return level;
    });

    const brokerFee: number = item.sellPrice * (percentage - (0.003 * brokersRelationsSkillLevel));
    console.log(item.sellPrice + '* (' + percentage + ' - (0.003 * ' + brokersRelationsSkillLevel + ') = ' + brokerFee);
    return Promise.resolve(brokerFee);
  }

  private async calculateJobInstallationCosts(bp: BlueprintModel): Promise<number> {
    if (!
      bp.locationId
    ) {
      return Promise.resolve(-1);
    }

    const systemId = await this.getSystemFromLocation(
      bp.id,
      this.userService.getCurrentUser().characterId,
      this.userService.getCurrentUser().accessToken).then(result => {
      return result;
    }).then(result => {
      return result;
    });

    const costIndice: number = await this.getCostIndice(systemId).then(result => {
      return result;
    });

    return Promise.resolve(bp.mineralBuyPrice * costIndice);

  }

  private async getSkillLevel(skillId: number, charId: number, accessToken: string): Promise<number> {
    return this.http.get<any>(
      EVE_CHARACTER_URI + '/' + charId + '/skills?token=' + accessToken
    ).toPromise().then(apiSkills => {
        return apiSkills.skills.filter(skill => skill.skill_id === skillId).map(skill => {
          return skill.active_skill_level;
        })
      }
    ).catch(error => {
        throw new Error('Unable to fetch data from EVE servers');
      }
    )
  }

  private async getStation(locationId: number): Promise<any> {
    return this.http.get<any>(
      EVE_UNIVERSE_URI + '/stations/' + locationId
    ).toPromise().catch(error => {
        throw new Error('Unable to fetch data from EVE servers');
      }
    );
  }

  private async getStructure(locationId: number, accessToken: string): Promise<any> {
    return this.http.get<any>(
      EVE_UNIVERSE_URI + '/structures/' + locationId + '?token=' + accessToken
    ).toPromise().catch(error => {
        throw new Error('Unable to fetch data from EVE servers');
      }
    );
  }

  private async getBuyPrice(typeId: number, buyMarketHub: MarketHubModel): Promise<any> {
    // buying means requesting the sell orders
    return this.getOrdersForLocation('sell', typeId, buyMarketHub.regionId, buyMarketHub.solarSystemId).then(
      result => {
        if (result) {
          let minOrder: any;
          let minPrice = 0;
          result.forEach(item => {
            if (minPrice === 0 || item.price < minPrice) {
              minOrder = item;
              minPrice = item.price;
            }
          });
          return minOrder;
        }
        return undefined;
      }
    );
  }

  private async getSellPrice(typeId: number, buyMarketHub: MarketHubModel): Promise<any> {
    // selling means requesting the buy orders
    const order: any = await this.getOrdersForLocation('buy', typeId, buyMarketHub.regionId, buyMarketHub.solarSystemId).then(
      result => {
        if (result) {
          let maxOrder: any;
          let maxPrice = 0;
          result.forEach(item => {
            if (item.price > maxPrice) {
              maxOrder = item;
              maxPrice = item.price;
            }
          });
          return maxOrder;
        }
        return undefined;
      }
    );
    return Promise.resolve(order);
  }

  private async getOrdersForLocation(type: string, itemId: number, regionId: number, locationId: number): Promise<any[]> {
    let orders: any[] = [];
    let keepSearching = true;
    let pageNr = 1;
    while (keepSearching) {
      const orderData: any[] = await this.http.get<any>(
        EVE_MARKET_URI + '/' +
        regionId + '/orders/?order_type=' +
        type + '&page=' +
        pageNr + '&type_id=' +
        itemId
      ).toPromise().then(ordersData => {
        keepSearching = ordersData.length;
        const filteredOrders = ordersData.filter(order => order.system_id === locationId);
        return filteredOrders;
      }).catch(error => {
          throw new Error('Unable to fetch data from EVE servers');
        }
      ).then(data => {
        return data;
      });
      orders = [...orders, ...orderData];
      pageNr++;
    }
    return Promise.resolve(orders);
  }

  private async getSystemFromLocation(bpId: number, charId: number, accessToken: string): Promise<number> {
    let pageNr = 0;
    let asset: any;
    do {
      pageNr++;
      asset = await this.getSystemFromLocationForPageNr(bpId, charId, accessToken, pageNr).then(result => {
        return result;
      });
    } while (pageNr < 1000 && !asset);

    if (asset) {
      if (asset.location_flag === 'Hangar') {
        let systemId = await this.getStation(asset.location_id).then(station => {
          return station.system_id;
        }).then(result => {
          return result;
        }).catch(error => {
          return undefined;
        });
        if (systemId) {
          return Promise.resolve(systemId);
        }
        systemId = await this.getStructure(asset.location_id, accessToken).then(station => {
          return station.solar_system_id;
        }).then(result => {
          return result;
        }).catch(error => {
          return undefined;
        });
        if (systemId) {
          return Promise.resolve(systemId);
        }
        return Promise.resolve(-1);
      } else {
        const systemId = await this.getSystemFromLocation(asset.location_id, charId, accessToken).then(result => {
          return result;
        });
        return Promise.resolve(systemId);
      }
    }
  }

  private async getSystemFromLocationForPageNr(id: number, charId: number, accessToken: string, pageNr: number): Promise<any> {
    return await this.http.get<any>(
      EVE_CHARACTER_URI + '/' +
      charId + '/assets?page=' +
      pageNr + '&token=' +
      accessToken
    ).toPromise().then(assetsData => {
      const data: any[] = assetsData.filter(assetData => assetData.type_id === id || assetData.item_id === id);
      if (data) {
        return data[0];
      }
      return undefined;
    }).catch(error => {
        throw new Error('Unable to fetch data from EVE servers');
      }
    );
  }

  private async getCostIndice(systemId: number): Promise<number> {
    return this.http.get<any>(
      EVE_INDUSTRY_URI + '/systems'
    ).toPromise().then(indices => {
      const filteredIndices: any[] = indices.filter(indice => indice.solar_system_id === systemId);
      const filteredCostIndeces: any[] = filteredIndices[0]
        .cost_indices.filter(indice => indice.activity === 'manufacturing')
      const resultingIndice = filteredCostIndeces[0].cost_index;
      return resultingIndice;
    }).catch(error => {
        throw new Error('Unable to fetch data from EVE servers');
      }
    );
  }

  private setStationId(locationId: number) {
    this.stationId = locationId;
  }
}
