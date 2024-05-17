import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthService } from '../../../../services/auth/auth.service';
import { ContentService } from '../../../../services/content/content.service';
import { User } from '../../../../interfaces/User';
import { ResponseContent } from '../../../../interfaces/ResponseContent';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { TitleCasePipe } from '@angular/common';
import { MessageComponent } from '../../../messages/message/message.component';
import { MessageService } from '../../../../services/messages/message.service';
import { Subscription } from 'rxjs';
import { environment } from '../../../../../environments/environment.development';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-content-list',
  standalone: true,
  imports: [RouterLink,  TitleCasePipe, MessageComponent, RouterOutlet],
  templateUrl: './content-list.component.html',
  styleUrl: './content-list.component.css'
})
export class ContentListComponent implements OnInit, OnDestroy {

  teacherContents! :ResponseContent[];
  errorMessage!: string;
  url:string = environment.urlClassroomApi + "/contents/image/";
  user!: User | null;
  contentToDelete!: ResponseContent;

  constructor(
    private contentService: ContentService,
    private tokenService: AuthService,
    private router: Router,
    private messageService: MessageService
  ){}

  ngOnDestroy(): void {
    this.messageService.clearMessages();
  }

  ngOnInit(): void {
    this.user = this.tokenService.getProfile();
    this.getTeacherContents();
  }

  getTeacherContents(){
    if (this.user != null) {
      this.contentService.getTeacherContents(this.user.id).subscribe({
        next: (contents) => {
          this.teacherContents = contents;
        },
        error: (error: HttpErrorResponse) => {
          console.log(error.status); // Now status should be correctly logged
          if (error.status === 404) {
            this.errorMessage = "You don't have any contents yet. Please add some.";
          } else {
            this.errorMessage = 'An error occurred: ' + error.message;
          }
          console.error(this.errorMessage);
        }
      });
    }
  }

  openEditForm(contentId: number) {
    this.router.navigateByUrl(`/sidebar-teacher/edit-content/${contentId}`);
  }

  openDeleteWindow(contentToDelete: ResponseContent) {
    const deleteModal = document.getElementById("deleteModal");

    if (deleteModal != null) {
      deleteModal.style.display = "block";
      this.contentToDelete = contentToDelete;
    }
  }

  closeDeleteWindow() {
    const deleteModal = document.getElementById("deleteModal");

    if (deleteModal != null) {
      deleteModal.style.display = "none";
    }
  }

  deleteContent(contentId: number) {
    const deleteModal = document.getElementById("deleteModal");
    
    this.contentService.deleteContent(contentId).subscribe(
      {
        next: (resp) => {
          this.sendMessage('Content deleted successfully');
          this.getTeacherContents();
        },
        error: (error) => {
          this.sendMessage('Unable to delete the content. Please, try again.');
        }
      }
    );

    if (deleteModal != null) {
      deleteModal.style.display = "none";
    }

  }

  sendMessage(message: string): void {
    this.messageService.sendMessage(message);
  }

  clearMessages(): void {
    this.messageService.clearMessages();
  }
  
}
