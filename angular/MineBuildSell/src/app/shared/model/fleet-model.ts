import {CharacterModel} from './character.model';
import {TypeModel} from './type-model';

export class FleetModel {

  constructor(public id: number,
              public name: string,
              public locationId: number,
              public owner: number,
              public type: TypeModel,
              public typeId: number,
              public start: string,
              public duration: number,
              public corporationRestricted: boolean,
              public invitations: CharacterModel[],
              public inviteText: string) {
  }

}
