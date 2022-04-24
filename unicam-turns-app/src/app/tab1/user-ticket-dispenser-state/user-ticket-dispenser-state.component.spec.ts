import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { UserTicketDispenserStateComponent } from './user-ticket-dispenser-state.component';

describe('UserTicketDispenserStateComponent', () => {
  let component: UserTicketDispenserStateComponent;
  let fixture: ComponentFixture<UserTicketDispenserStateComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ UserTicketDispenserStateComponent ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(UserTicketDispenserStateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
