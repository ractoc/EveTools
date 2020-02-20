export class ItemModel {

  constructor(public id: number,
              public name: string,
              public buyPrice: number,
              public sellPrice: number,
              public salesTax: number,
              public brokerFee: number) {
  }

}
