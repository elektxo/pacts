import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExplorerCourseComponent } from './explorer-course.component';

describe('ExplorerCourseComponent', () => {
  let component: ExplorerCourseComponent;
  let fixture: ComponentFixture<ExplorerCourseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExplorerCourseComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExplorerCourseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
