import { Injectable } from '@angular/core';
import { Observable, Subject, take, timer } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private subject = new Subject<any>();
  
  sendMessage(message: string) {
    this.clearMessages();
    this.subject.next({ text: message, timestamp: Date.now() });
    timer(5000).pipe(take(1)).subscribe(() => {
      this.clearMessage(message);
    });
  }

  clearMessage(message: string) {
    this.subject.next({ text: message, type: 'clear' });
  }

  clearMessages() {
    this.subject.next(null);
  }

  onMessage(): Observable<any> {
    return this.subject.asObservable();
  }
}
