import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TicketDispenserComponent } from './submodules/ticket-dispenser/ticket-dispenser.component';
import { Tab2Page } from './tab2.page';

const routes: Routes = [
  {
    path: '',
    component: Tab2Page,
  },
  {
    path: 'dispenser/:dispenserId',
    component: TicketDispenserComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class Tab2PageRoutingModule {}
