import {Role} from "./role";

export class Type {

  constructor(public id: number,
              public name: string,
              public description: string,
              public roles: Role[]) {
  }
}
