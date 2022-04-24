import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Tab1Page } from './tab1.page';
import { UserTicketDispenserStateComponent } from './user-ticket-dispenser-state/user-ticket-dispenser-state.component';

const routes: Routes = [
  {
    path: '',
    component: Tab1Page,
  },
  {
    path: 'dispenser/:dispenserId',
    component: UserTicketDispenserStateComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class Tab1PageRoutingModule {}
