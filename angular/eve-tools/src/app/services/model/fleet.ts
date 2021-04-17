import {Type} from "./type";

export class Fleet {

  constructor(public id: number,
              public name: string,
              public description: string,
              public locationId: number,
              public owner: number,
              public type: Type,
              public start: string,
              public duration: number,
              public solarsystemId: number,
              public restricted: boolean) {
  }

}
