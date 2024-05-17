import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContentService } from '../../../services/content/content.service';
import { AuthService } from '../../../services/auth/auth.service';
import { RequestContent } from '../../../interfaces/RequestContent';
import { ActivatedRoute, Router } from '@angular/router';
import { first } from 'rxjs';
import { MessageService } from '../../../services/messages/message.service';

@Component({
  selector: 'app-content-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './content-form.component.html',
  styleUrl: './content-form.component.css'
})
export class ContentFormComponent implements OnInit{

  id!: number;
  isCreate!: boolean;

  fileFormControl: FormControl<File> = new FormControl();

  contentForm = this.formBuilder.group({
    title : ['', Validators.required],
    description: ['', Validators.required],
    estimatedHours: [0, [Validators.required, Validators.min(1)]],
    teacherId: [''],
    file: this.fileFormControl
  })

  constructor(
    private formBuilder : FormBuilder, 
    private contentService: ContentService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService
  ){}

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.isCreate = !this.id;

    if (!this.isCreate) {
      this.contentService.getContentById(this.id)
        .pipe(first())
        .subscribe( resp => this.contentForm.patchValue(resp));
    }
  }

  get title() {
    return this.contentForm.controls.title
  }

  get description() {
    return this.contentForm.controls.description
  }

  get estimatedHours() {
    return this.contentForm.controls.estimatedHours
  }
 
  get file(): FormControl<File> {
    return this.file;
  }

  saveContent() {
    if (this.contentForm.invalid) {
      return;
    }
    if (this.isCreate) {
      this.createContent();
    } else {
      this.updateContent();
    }
  }

  createContent() {
    
    const formData = new FormData();

    let content : RequestContent = {
      title : this.contentForm.value.title!,
      description : this.contentForm.value.description!,
      estimatedHours: this.contentForm.value.estimatedHours!,
      teacherId: this.authService.getProfile()!.id
    }

    formData.append("content", new Blob([JSON.stringify(content)], {type: 'application/json'}));
    formData.append("image", this.contentForm.value.file!);
    console.log(this.contentForm.value.file);

    this.contentService.createContent(formData)
      .pipe(first())  
      .subscribe({
        next : response => {
          this.router.navigateByUrl('sidebar-teacher/contents');
          this.messageService.sendMessage('Content created successfully');
        },
        error : error => {
          this.router.navigateByUrl('sidebar-teacher/contents');
          this.messageService.sendMessage('Unable to create the content. Please, try again.');
        }
    });
  }

  updateContent() {

    const formData = new FormData();

    let content : RequestContent = {
      title : this.contentForm.value.title!,
      description : this.contentForm.value.description!,
      estimatedHours: this.contentForm.value.estimatedHours!,
      teacherId: this.authService.getProfile()!.id
    }

    formData.append("content", new Blob([JSON.stringify(content)], {type: 'application/json'}));
    if(this.contentForm.value.file == null){
      this.contentForm.value.file = new File([""], "empty");
    }
    formData.append("image", this.contentForm.value.file!);

    this.contentService.updateContent(this.id, formData)
      .pipe(first())
      .subscribe({
        next: response => {
          this.router.navigateByUrl('sidebar-teacher/contents');
          this.messageService.sendMessage('Content updated successfully');
        },
        error : error => {
          this.router.navigateByUrl('sidebar-teacher/contents');
          this.messageService.sendMessage('Unable to update the content. Please, try again.');
        }
      });
  }

  uploadFile($event: Event) {

    const target = $event.target as HTMLInputElement;
    const file: File | null = target.files![0];
  
    if(file != null) {
      this.contentForm.value.file = file;
    }
    console.log(file);
  }

}
