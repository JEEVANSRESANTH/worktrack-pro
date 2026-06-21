import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CopilotService } from './services/copilot';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  prompt = '';
  aiResponse = signal('');
  isLoading = signal(false);

  constructor(private copilotService: CopilotService) {}

  submitCommand(): void {
    const trimmedPrompt = this.prompt.trim();

    if (!trimmedPrompt || this.isLoading()) {
      return;
    }

    this.isLoading.set(true);

    this.copilotService.sendCommand(trimmedPrompt).subscribe({
      next: (res: string) => {
        this.aiResponse.set(res);
        this.prompt = '';
        this.isLoading.set(false);
      },
      error: () => {
        this.aiResponse.set('Error: Backend service unreachable.');
        this.isLoading.set(false);
      }
    });
  }
}
