import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessagePayload } from 'firebase/messaging';
import { Ticket } from '../domain/ticket';
import { Firebase } from '../service/firebase';
import { UserApi } from '../service/user-api';

@Component({
  selector: 'app-tab1',
  templateUrl: 'tab1.page.html',
  styleUrls: ['tab1.page.scss']
})
export class Tab1Page implements OnInit {

  public tickets : Ticket[] = [];
  public openingDispenserId;

  constructor(
    private userApi : UserApi,
    private router : Router,
    private activatedRoute : ActivatedRoute,
    private firebase : Firebase
    ) {
      firebase.subscribe(payload => this.messageReceived(payload));
    }

  ngOnInit(): void {
    this.ionViewWillEnter();
  }

  /**
   * @see https://ionicframework.com/docs/angular/lifecycle
   */
  ionViewWillEnter() : void {
    this.reloadList();
  }  

  reloadList() {
    this.userApi.currentUserTickets().then(response => this.tickets = response.payload);
  }

  openDispenserDetailBy(id : number) {
    if (! this.firebase.initialized) {
      this.firebase.initialize();
    }
    this.router.navigate(['dispenser/' + id], {relativeTo : this.activatedRoute});
  }

  openDispenserDetail() {
    this.openDispenserDetailBy(this.openingDispenserId);
    this.openingDispenserId = null;
  }

  messageReceived(messagePayload : MessagePayload) {
    if (messagePayload.data['tag'] === 'yourTicketCalled') {
      let id = parseInt(messagePayload.data['ticketDispenserId']);
      this.openDispenserDetailBy(id);
    }
  }

}
