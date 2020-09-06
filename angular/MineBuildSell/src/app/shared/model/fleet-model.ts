export class FleetModel {

  constructor(public id: number,
              public name: string,
              public locationId: number,
              public owner: number,
              public type: string,
              public start: string,
              public duration: number) {
  }

}
