import {ItemModel} from './item.model';

export class BlueprintModel {

  constructor(public id: number,
              public name: string,
              public locationId: number,
              public buyPrice: number,
              public materialEfficiency: number,
              public runs: number,
              public manufacturingMaterials: BlueprintMaterialModel[],
              public mineralBuyPrice: number,
              public mineralSellPrice: number,
              public jobInstallationCosts: number,
              public item: ItemModel) {
  }

}

export class BlueprintMaterialModel {

  constructor(public typeId: number,
              public quantity: number,
              public calculatedTotalQuantity: number,
              public buyPrice: number,
              public sellPrice: number) {
  }

}
