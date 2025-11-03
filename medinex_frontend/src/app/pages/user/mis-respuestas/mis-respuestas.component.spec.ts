import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisRespuestasComponent } from './mis-respuestas.component';

describe('MisRespuestasComponent', () => {
  let component: MisRespuestasComponent;
  let fixture: ComponentFixture<MisRespuestasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MisRespuestasComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MisRespuestasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
