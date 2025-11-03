import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewDoctoresComponent } from './view-doctores.component'; 

describe('ViewDoctoresesComponent', () => {
  let component: ViewDoctoresComponent;
  let fixture: ComponentFixture<ViewDoctoresComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewDoctoresComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewDoctoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
