import { Component, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { MessageService } from '../../../services/messages/message.service';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [],
  templateUrl: './message.component.html',
  styleUrl: './message.component.css'
})
export class MessageComponent implements OnDestroy {

  messages: { text: string, timestamp: number }[] = [];
  subscription:Subscription = new Subscription;

  constructor(private messageService: MessageService) {
    this.subscription = this.messageService.onMessage().subscribe(data => {
      if (data) {
        if (data.type === 'clear') {
          this.messages = this.messages.filter(m => m.text !== data.text);
        } else {
          this.messages.push(data);
        }
      } else {
        this.messages = [];
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  closeMessage($index:number) {
    this.messages.splice($index, 1);
  }

}