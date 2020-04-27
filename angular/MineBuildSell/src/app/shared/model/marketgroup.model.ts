export class MarketGroupModel {

  constructor(public id: number,
              public name: string,
              public children: MarketGroupModel[],
              public isCollapsed = true) {
  }

}
