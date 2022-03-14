import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Ticket } from '../domain/ticket';

@Component({
  selector: 'app-tab1',
  templateUrl: 'tab1.page.html',
  styleUrls: ['tab1.page.scss']
})
export class Tab1Page {

  private apiUrl : string = "http://localhost:8080";
  public ticket : Ticket = {} as Ticket;

  constructor(public httpClient : HttpClient) {}

  public withraw() : void {
    console.log("withraw");
    this.httpClient.get<Ticket>(this.apiUrl + "/user/dispenser/1/withraw").subscribe(t => {
      this.ticket = t;
      console.log(t);
    });
  }

}
