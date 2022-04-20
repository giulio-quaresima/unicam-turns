import { Component } from '@angular/core';
import { Ticket } from '../domain/ticket';
import { Api } from '../service/api';

@Component({
  selector: 'app-tab1',
  templateUrl: 'tab1.page.html',
  styleUrls: ['tab1.page.scss']
})
export class Tab1Page {

  public ticket : Ticket = {} as Ticket;

  constructor(public api : Api) {}

  public withraw() : void {
    this.api.whoami();
    // this.api.withraw(1).then(t => this.ticket = t); // FIXME id hard-coded
  }

}
