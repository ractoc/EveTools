import {FleetModel} from './fleet-model';

export class RegistrationModel {

  constructor(public id: number,
              public fleet: FleetModel,
              public name: string,
              public characterName: string) {
  }

}
