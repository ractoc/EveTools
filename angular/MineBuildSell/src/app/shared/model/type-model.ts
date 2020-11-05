import {RoleModel} from "./role.model";

export class TypeModel {

  constructor(public id: number,
              public name: string,
              public description: string,
              public roles: RoleModel[]) {
  }
}
