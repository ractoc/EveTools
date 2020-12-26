import {FleetModel} from './fleet-model';

export class InviteModel {

  constructor(public key: string,
              public fleet: FleetModel,
              public name: string,
              public additionalInfo: string,
              public id: number,
              public icon: string) {
  }

}
