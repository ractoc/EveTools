import {FleetModel} from './fleet-model';

export class InviteModel {

  constructor(public key: string,
              public fleet: FleetModel,
              public name: string,
              public id: number,
              public icon: string,
              public type: string) {
  }

}
