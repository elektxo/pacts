import { Component, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <form [formGroup]="searchForm">
      <input class="form-control mr-sm-2" formControlName="searchTerm" type="search" placeholder="Search" aria-label="Search">
    </form>
  `
})
export class SearchComponent {
  searchForm: FormGroup;

  @Output() searchEvent = new EventEmitter<string>();

  constructor(private formBuilder: FormBuilder) {
    this.searchForm = this.formBuilder.group({
      searchTerm: ['']
    });

    this.searchForm.get('searchTerm')!.valueChanges.subscribe(value => {
      this.searchEvent.emit(value);
    });
  }
}
