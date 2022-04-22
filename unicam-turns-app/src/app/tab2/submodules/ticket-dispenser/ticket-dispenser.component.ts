import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Session } from 'src/app/domain/session';
import { TicketDispenser } from 'src/app/domain/ticket-dispenser';
import { OwnerApi } from 'src/app/service/owner-api';

@Component({
  selector: 'app-ticket-dispenser',
  templateUrl: './ticket-dispenser.component.html',
  styleUrls: ['./ticket-dispenser.component.scss'],
})
export class TicketDispenserComponent implements OnInit {

  public ticketDispenser : TicketDispenser = {} as TicketDispenser;
  public currentSession : Session = {} as Session;

  constructor(
    private ownerApi : OwnerApi,
    private router : Router,
    private activatedRoute : ActivatedRoute
    ) {}

  ngOnInit() {
    let dispenserId : number = parseInt(this.activatedRoute.snapshot.paramMap.get("dispenserId"));
    console.log(dispenserId);
    this.ownerApi.dispenser(dispenserId).then(response => this.ticketDispenser = response.payload);
    this.ownerApi.currentSession(dispenserId).then(response => this.currentSession = response.payload);
  }

  startSession() {
    this.ownerApi.startSession(this.ticketDispenser.id).then(response => {this.currentSession = response.payload; console.log(this.currentSession)});
  }

  endSession() {
    this.ownerApi.endSession(this.ticketDispenser.id).then(response => {this.currentSession = response.payload; console.log(this.currentSession)});
  }

  close() {
    this.router.navigate(['../..'], {relativeTo : this.activatedRoute});
  }

}
