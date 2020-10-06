import {FleetModel} from './fleet-model';

export class InviteModel {

  constructor(public key: number,
              public fleet: FleetModel,
              public name: string,
              public characterName: string,
              public additionalInfo: string) {
  }

}
