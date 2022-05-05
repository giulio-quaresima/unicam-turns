import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
  public showActivateNotificationsButton : boolean = false;

  constructor(
    private userApi : UserApi,
    private router : Router,
    private activatedRoute : ActivatedRoute,
    private firebase : Firebase
    ) {}

  ngOnInit(): void {
    if (this.firebase.supported) {
      this.showActivateNotificationsButton = true;
    }
    this.ionViewWillEnter();
  }

  activateNotifications() : boolean {
    if (this.showActivateNotificationsButton) {
      this.showActivateNotificationsButton = false;
      this.firebase.sendTokenToBackend();
    }
    return this.firebase.supported;
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
    this.router.navigate(['dispenser/' + id], {relativeTo : this.activatedRoute});
  }

  openDispenserDetail() {
    this.openDispenserDetailBy(this.openingDispenserId);
    this.openingDispenserId = null;
  }

}
