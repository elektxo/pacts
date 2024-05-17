import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeachercourseviewComponent } from './teachercourseview.component';

describe('TeachercourseviewComponent', () => {
  let component: TeachercourseviewComponent;
  let fixture: ComponentFixture<TeachercourseviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeachercourseviewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TeachercourseviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
