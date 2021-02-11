export class Invitation {

  constructor(public id: number,
              public fleetId: number,
              public inviteeId: number,
              public type: string,
              public name: string,
              public icon: string) {
  }

}
