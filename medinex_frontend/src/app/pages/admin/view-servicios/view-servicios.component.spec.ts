import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewServiciosComponent } from './view-servicios.component';

describe('ViewServiciosComponent', () => {
  let component: ViewServiciosComponent;
  let fixture: ComponentFixture<ViewServiciosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewServiciosComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewServiciosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
