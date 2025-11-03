import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewRespuestasComponent } from './view-respuestas.component';

describe('ViewRespuestasComponent', () => {
  let component: ViewRespuestasComponent;
  let fixture: ComponentFixture<ViewRespuestasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewRespuestasComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewRespuestasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
