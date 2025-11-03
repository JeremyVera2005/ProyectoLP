import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadDoctorComponent } from './load-doctor.component';

describe('LoadDoctorComponent', () => {
  let component: LoadDoctorComponent;
  let fixture: ComponentFixture<LoadDoctorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoadDoctorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoadDoctorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
