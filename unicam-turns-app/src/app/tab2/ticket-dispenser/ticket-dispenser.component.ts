import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessagePayload } from 'firebase/messaging';
import { Session } from 'src/app/domain/session';
import { TicketDispenser } from 'src/app/domain/ticket-dispenser';
import { Firebase } from 'src/app/service/firebase';
import { OwnerApi } from 'src/app/service/owner-api';

@Component({
  selector: 'app-ticket-dispenser',
  templateUrl: './ticket-dispenser.component.html',
  styleUrls: ['./ticket-dispenser.component.scss'],
})
export class TicketDispenserComponent implements OnInit {

  public ticketDispenser? : TicketDispenser = {} as TicketDispenser;
  public currentSession? : Session = {} as Session;

  constructor(
    private ownerApi : OwnerApi,
    private router : Router,
    private activatedRoute : ActivatedRoute,
    private firebase : Firebase
    ) {
      firebase.subscribe(payload => this.messageReceived(payload));
    }

  ngOnInit() {
    this.ionViewWillEnter();
  }
  
  /**
  * @see https://ionicframework.com/docs/angular/lifecycle
  */
  ionViewWillEnter() : void {
    let dispenserId : number = parseInt(this.activatedRoute.snapshot.paramMap.get("dispenserId"));
    this.reloadBy(dispenserId);
  }

  reloadBy(dispenserId : number) {
    this.ownerApi.dispenser(dispenserId).then(response => this.ticketDispenser = response.payload);
    this.ownerApi.lastSession(dispenserId).then(response => this.currentSession = response.payload);
  }

  reload() {
    if (this.ticketDispenser.id) {
      this.reloadBy(this.ticketDispenser.id);
    }
  }

  sessionStarted() : boolean {
    return !!this.currentSession && this.currentSession.open;
  }

  startSession() {
    this.ownerApi.startSession(this.ticketDispenser.id).then(response => {this.currentSession = response.payload; console.log(this.currentSession)});
  }

  drawTicket() {
    this.ownerApi.drawTicket(this.ticketDispenser.id).then(response => {this.currentSession = response.payload; console.log(this.currentSession)});
  }

  endSession() {
    this.ownerApi.endSession(this.ticketDispenser.id).then(response => {this.currentSession = response.payload; console.log(this.currentSession)});
  }

  close() {
    this.router.navigate(['../..'], {relativeTo : this.activatedRoute});
  }

  messageReceived(messagePayload : MessagePayload) {
    if (messagePayload.data['tag'] === 'ticketDispenserToggle') {
      if (parseInt(messagePayload.data['ticketDispenserId']) === this.ticketDispenser.id) {
        this.reload();
      }
    }
  }

  public get dispenserUserUrl() : string {
    if (!! this.ticketDispenser.id) {
      return window.location.origin + "/tabs/tab1/dispenser/" + this.ticketDispenser.id;
    }
    return null;
  }

}
