import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { UserTicketDispenserState } from 'src/app/domain/user-ticket-dispenser-state';
import { Response } from 'src/app/service/response';
import { UserApi } from 'src/app/service/user-api';

@Component({
  selector: 'app-user-ticket-dispenser-state',
  templateUrl: './user-ticket-dispenser-state.component.html',
  styleUrls: ['./user-ticket-dispenser-state.component.scss'],
})
export class UserTicketDispenserStateComponent implements OnInit {

  public userTicketDispenserState : UserTicketDispenserState = {} as UserTicketDispenserState;

  constructor(
    private userApi : UserApi,
    private router : Router,
    private activatedRoute : ActivatedRoute,
    public alertController: AlertController
  ) { }

  ngOnInit() {
    let dispenserId : number = parseInt(this.activatedRoute.snapshot.paramMap.get("dispenserId"));
    this.userApi.currentUserTicketDispenserState(dispenserId).then(response => {
      if (response.success) {
        this.userTicketDispenserState = response.payload;
      } else {
        this.alertNotFound("Il distributore n. " + dispenserId + " non esiste!");
      }
    });
  }

  withdraw() {
    this.userApi.withdraw(this.userTicketDispenserState.ticketDispenser.id).then(response => this.userTicketDispenserState = response.payload);
  }

  async alertNotFound(message : string) {

    const alert = await this.alertController.create({
      header : "Distributore inesistente",
      message : message,
      buttons : ["Chiudi"]
    });

    await alert.present();
    await alert.onDidDismiss();

    this.close();

  }

  close() {
    this.router.navigate(['../..'], {relativeTo : this.activatedRoute});
  }

}
