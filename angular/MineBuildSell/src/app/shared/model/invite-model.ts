import {FleetModel} from './fleet-model';

export class InviteModel {

  constructor(public key: string,
              public fleet: FleetModel,
              public name: string,
              public characterName: string,
              public additionalInfo: string,
              public corporationId: number,
              public characterId: number,
              public loadingIcon: boolean,
              public icon: string) {
  }

}
