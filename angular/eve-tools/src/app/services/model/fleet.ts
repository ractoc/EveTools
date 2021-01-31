import {Role} from './role';
import {Type} from "./type";
import {Character} from "./character";

export class Fleet {

  constructor(public id: number,
              public name: string,
              public description: string,
              public locationId: number,
              public owner: number,
              public type: Type,
              public typeId: number,
              public start: string,
              public duration: number,
              public restricted: boolean,
              public invitations: Character[],
              public roles: Role[]) {
  }

}
