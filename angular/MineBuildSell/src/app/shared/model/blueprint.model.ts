export class BlueprintModel {

  constructor(public id: number,
              public name: string,
              public materialEfficiency: number,
              public quantity: number,
              public runs: number,
              public timeEfficiency: number,
              public locationId: number) {
  }

}
