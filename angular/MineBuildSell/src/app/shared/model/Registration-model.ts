import {FleetModel} from './fleet-model';

export class RegistrationModel {

  constructor(public fleetId: number,
              public characterId: number,
              public fleet: FleetModel,
              public name: string,
              public characterName: string,
              public loadingIcon: boolean,
              public icon: string,
              public roleId: number,
              public start: string,
              public end: string) {
  }

}
