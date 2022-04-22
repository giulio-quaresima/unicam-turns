import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketDispenser } from '../domain/ticket-dispenser';
import { OwnerApi } from '../service/owner-api';

@Component({
  selector: 'app-tab2',
  templateUrl: 'tab2.page.html',
  styleUrls: ['tab2.page.scss']
})
export class Tab2Page implements OnInit {

  public ticketDispensers : TicketDispenser[];

  public newTicketDispenserLabel : string;

  constructor(
    private ownerApi : OwnerApi,
    private router : Router,
    private activatedRoute : ActivatedRoute
    ) {}
  
  ngOnInit() : void {
    console.log("adsfiajeolerujtqaerogioijaerogiajeri");
    this.reloadList();
  }

  /**
   * @see https://ionicframework.com/docs/angular/lifecycle
   */
  ionViewWillEnter() : void {
    this.ngOnInit();
  }  

  reloadList() : void {
    this.ownerApi.ticketDispensersList().then(response => this.ticketDispensers = response.payload);
  }

  createTicketDispenser() : void {
    this.ownerApi.createTicketDispenser(this.newTicketDispenserLabel).then(response => {
      if (response.success) {
        this.ticketDispensers.push(response.payload)
        this.newTicketDispenserLabel = "";
      }
    });
  }

  openDispenserDetail(id : number) {
    this.router.navigate(['dispenser/' + id], {relativeTo : this.activatedRoute});
  }

}
