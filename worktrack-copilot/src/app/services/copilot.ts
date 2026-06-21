import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CopilotService {

  constructor(private http: HttpClient) { }

  sendCommand(prompt: string): Observable<string> {
    return this.http.post('/api/ai/command', prompt, {
      responseType: 'text'
    });
  }
}
