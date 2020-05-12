import {ItemModel} from './item.model';

export class BlueprintModel {

  constructor(public id: number,
              public name: string,
              public buyPrice: number,
              public materialEfficiency: number,
              public runs: number,
              public mineralBuyPrice: number,
              public mineralSellPrice: number,
              public jobInstallationCosts: number,
              public item: ItemModel) {
  }

}
