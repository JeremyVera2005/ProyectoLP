import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmergenciasComponent } from './emergencias.component';

describe('EmergenciasComponent', () => {
  let component: EmergenciasComponent;
  let fixture: ComponentFixture<EmergenciasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmergenciasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmergenciasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
