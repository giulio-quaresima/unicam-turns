import { Http, HttpResponse } from '@capacitor-community/http';
import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Ticket } from '../domain/ticket';

@Component({
  selector: 'app-tab1',
  templateUrl: 'tab1.page.html',
  styleUrls: ['tab1.page.scss']
})
export class Tab1Page {

  private apiUrl : string = "http://10.0.2.2:8080"; // FIXME 10.0.2.2 è per l'emulatore android, vedi https://developer.android.com/studio/run/emulator-networking
  public ticket : Ticket = {} as Ticket;

  constructor() {}

  public withraw() : void {
    console.log("withraw");
    Http.get({url : this.apiUrl + "/user/dispenser/1/withraw"}).then(response => {
      if (response.status == 200) {
        this.ticket = response.data;
      }
    });
    /*
    this.httpClient.get<Ticket>(this.apiUrl + "/user/dispenser/1/withraw").subscribe(t => {
      this.ticket = t;
      console.log(t);
    });
    */
  }

}
