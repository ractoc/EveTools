export class User {
  constructor(public eveState: string,
              public name: string,
              public roles: string[],
              public characterId: number,
              public accessToken: string) {
  }

  hasRole(search: string) {
    return this.roles.find(role => role.toLowerCase() === search.toLowerCase()) !== undefined;
  }
}
