import { IonicModule } from '@ionic/angular';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Tab1Page } from './tab1.page';
import { ExploreContainerComponentModule } from '../explore-container/explore-container.module';

import { Tab1PageRoutingModule } from './tab1-routing.module';
import { UserTicketDispenserStateComponent } from './user-ticket-dispenser-state/user-ticket-dispenser-state.component';
import { QRCodeModule } from 'angularx-qrcode';

@NgModule({
  imports: [
    IonicModule,
    CommonModule,
    FormsModule,
    ExploreContainerComponentModule,
    Tab1PageRoutingModule,
    QRCodeModule
  ],
  declarations: [Tab1Page, UserTicketDispenserStateComponent]
})
export class Tab1PageModule {}
