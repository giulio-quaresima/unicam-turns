import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { MessagePayload } from 'firebase/messaging';
import { UserTicketDispenserState } from 'src/app/domain/user-ticket-dispenser-state';
import { Firebase } from 'src/app/service/firebase';
import { Response } from 'src/app/service/response';
import { UserApi } from 'src/app/service/user-api';

@Component({
  selector: 'app-user-ticket-dispenser-state',
  templateUrl: './user-ticket-dispenser-state.component.html',
  styleUrls: ['./user-ticket-dispenser-state.component.scss'],
})
export class UserTicketDispenserStateComponent implements OnInit {

  public userTicketDispenserState = {} as UserTicketDispenserState;
  public itsYourTurn = false;
  public youLostYourTurn = false;

  constructor(
    private userApi : UserApi,
    private router : Router,
    private activatedRoute : ActivatedRoute,
    public alertController: AlertController,
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
    this.reload();
  } 
  
  withdraw() {
    this.userApi.withdraw(this.userTicketDispenserState.ticketDispenser.id).then(response => this.reloadWith(response.payload));
  }
  
  reload() {
    let dispenserId : number = parseInt(this.activatedRoute.snapshot.paramMap.get("dispenserId"));
    this.reloadBy(dispenserId);
  }
  
  reloadBy(dispenserId : number) {
    this.userApi.currentUserTicketDispenserState(dispenserId).then(response => {
      if (response.success) {
        this.reloadWith(response.payload);
      } else {
        this.alertNotFound("Il distributore n. " + dispenserId + " non esiste!");
      }
    });
  }

  reloadWith(userTicketDispenserState : UserTicketDispenserState) {
    this.userTicketDispenserState = userTicketDispenserState;
    this.itsYourTurn = false;
    this.youLostYourTurn = false;
    if (this.userTicketDispenserState.currentUserAliveTicket) {
      if (this.userTicketDispenserState.lastDrewTicket) {
        this.itsYourTurn = this.userTicketDispenserState.lastDrewTicket && this.userTicketDispenserState.lastDrewTicket.publicNumber === this.userTicketDispenserState.currentUserAliveTicket.publicNumber;
        this.youLostYourTurn = this.userTicketDispenserState.lastDrewTicket && this.userTicketDispenserState.lastDrewTicket.publicNumber > this.userTicketDispenserState.currentUserAliveTicket.publicNumber;
      }
    }
    console.log("UserTicketDispenserState: ", this.userTicketDispenserState);
  }

  messageReceived(messagePayload : MessagePayload) {
    if (messagePayload.data['tag'] === 'ticketDispenserToggle') {
      if (parseInt(messagePayload.data['ticketDispenserId']) === this.userTicketDispenserState?.ticketDispenser?.id) {
        this.reload();
      }
    } else if (messagePayload.data['tag'] === 'yourTicketCalled') {
      if (parseInt(messagePayload.data['ticketDispenserId']) === this.userTicketDispenserState?.ticketDispenser?.id) {
        this.reload();
      } else {
        this.router.navigate(['/tabs/tab1/dispenser/' + messagePayload.data['ticketDispenserId']], {});
      }
    }
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
