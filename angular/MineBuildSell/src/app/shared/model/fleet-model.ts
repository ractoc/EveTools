import {CharacterModel} from './character.model';
import {TypeModel} from './type-model';
import {RoleModel} from './role.model';

export class FleetModel {

  constructor(public id: number,
              public name: string,
              public description: string,
              public locationId: number,
              public owner: number,
              public type: TypeModel,
              public typeId: number,
              public start: string,
              public duration: number,
              public restricted: boolean,
              public invitations: CharacterModel[],
              public roles: RoleModel[]) {
  }

}
