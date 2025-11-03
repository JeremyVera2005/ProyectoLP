import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewDoctorPreguntasComponent } from './view-doctor-preguntas.component';

describe('ViewDoctorPreguntasComponent', () => {
  let component: ViewDoctorPreguntasComponent;
  let fixture: ComponentFixture<ViewDoctorPreguntasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewDoctorPreguntasComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewDoctorPreguntasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
