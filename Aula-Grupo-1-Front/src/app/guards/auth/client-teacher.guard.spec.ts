import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { clientTeacherGuard } from './client-teacher.guard';

describe('clientTeacherGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => clientTeacherGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
