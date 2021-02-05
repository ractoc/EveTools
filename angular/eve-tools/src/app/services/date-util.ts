export class DateUtil {

  static parseDate(start: string): Date {
    let startJson = JSON.parse(start);
    let dateString =
      startJson.date.year + '-' + startJson.date.month + '-' + startJson.date.day +
      'T'
      + startJson.time.hour + ':' + startJson.time.minute + ':' + startJson.time.second
    console.log('dateString', dateString);
    let startDate: Date = new Date(Date.UTC(startJson.date.year, startJson.date.month - 1, startJson.date.day, startJson.time.hour, startJson.time.minute, startJson.time.second));
    console.log('startDate', startDate);
    return startDate;
  }

  static formatDate(start: Date): string {
    return "{\"date\":{\"year\":" +
      start.getFullYear() +
      ",\"month\":" +
      (start.getMonth() + 1) +
      ",\"day\":" +
      start.getDate() +
      "},\"time\":{\"hour\":" +
      start.getUTCHours() +
      ",\"minute\":" +
      start.getMinutes() +
      ",\"second\":" +
      start.getSeconds() +
      "}}";
  };

}
