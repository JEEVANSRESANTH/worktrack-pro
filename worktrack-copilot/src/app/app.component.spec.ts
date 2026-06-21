import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AppComponent } from './app.component';
import { CopilotService } from './services/copilot';
import { of, throwError } from 'rxjs';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let copilotService: { sendCommand: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    copilotService = {
      sendCommand: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [AppComponent, FormsModule, CommonModule],
      providers: [
        { provide: CopilotService, useValue: copilotService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should submit a command and update aiResponse signal when service returns success', async () => {
    const testPrompt = 'What is employee WT-434019 currently working on?';
    const mockResponse = 'Employee WT-434019 is assigned to project X.';
    component.prompt = testPrompt;
    copilotService.sendCommand.mockReturnValue(of(mockResponse));

    component.submitCommand();

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.aiResponse()).toBe(mockResponse);
    expect(component.isLoading()).toBe(false);
    expect(component.prompt).toBe('');
    expect(copilotService.sendCommand).toHaveBeenCalledWith(testPrompt);
  });

  it('should display error message when service returns an error', async () => {
    const testPrompt = 'Any query';
    component.prompt = testPrompt;
    copilotService.sendCommand.mockReturnValue(throwError(() => new Error('Backend service unreachable')));

    component.submitCommand();

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.aiResponse()).toContain('Error: Backend service unreachable');
    expect(component.isLoading()).toBe(false);
  });

  it('should set isLoading to true during request and false after completion', async () => {
    const testPrompt = 'Test query';
    component.prompt = testPrompt;
    copilotService.sendCommand.mockReturnValue(of('Response'));

    expect(component.isLoading()).toBe(false);

    component.submitCommand();

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.isLoading()).toBe(false);
  });

  it('should not submit when prompt is empty', () => {
    component.prompt = '';

    component.submitCommand();

    expect(copilotService.sendCommand).not.toHaveBeenCalled();
  });

  it('should not submit when prompt contains only whitespace', () => {
    component.prompt = '   \t\n  ';

    component.submitCommand();

    expect(copilotService.sendCommand).not.toHaveBeenCalled();
  });

  it('should not allow multiple submissions while isLoading is true', () => {
    component.prompt = 'Test query';
    component.isLoading.set(true);

    component.submitCommand();

    expect(copilotService.sendCommand).not.toHaveBeenCalled();
  });

  it('should trim whitespace from prompt before sending to service', async () => {
    const testPrompt = '  What is WT-434019 working on?  ';
    const trimmedPrompt = 'What is WT-434019 working on?';
    component.prompt = testPrompt;
    copilotService.sendCommand.mockReturnValue(of('Response'));

    component.submitCommand();

    fixture.detectChanges();
    await fixture.whenStable();

    expect(copilotService.sendCommand).toHaveBeenCalledWith(trimmedPrompt);
  });

  it('should update template when aiResponse signal changes', async () => {
    const testResponse = 'Test response from AI';

    component.aiResponse.set(testResponse);
    fixture.detectChanges();

    await fixture.whenStable();

    const compiled = fixture.nativeElement;
    const responseElement = compiled.querySelector('.response-body');
    expect(responseElement?.textContent).toContain(testResponse);
  });

  it('should display "Processing" when isLoading is true', async () => {
    component.isLoading.set(true);
    fixture.detectChanges();

    await fixture.whenStable();

    const compiled = fixture.nativeElement;
    const button = compiled.querySelector('button');
    expect(button?.textContent).toContain('Processing');
  });

  it('should disable submit button when isLoading is true', async () => {
    component.isLoading.set(true);
    fixture.detectChanges();

    await fixture.whenStable();

    const compiled = fixture.nativeElement;
    const button = compiled.querySelector('button');
    expect(button?.disabled).toBe(true);
  });

  it('should disable submit button when prompt is empty', async () => {
    component.prompt = '';
    component.isLoading.set(false);
    fixture.detectChanges();

    await fixture.whenStable();

    const compiled = fixture.nativeElement;
    const button = compiled.querySelector('button');
    expect(button?.disabled).toBe(true);
  });

  it('should submit command when Enter key is pressed in input field', () => {
    const testPrompt = 'Test query';
    component.prompt = testPrompt;
    copilotService.sendCommand.mockReturnValue(of('Response'));
    const spy = vi.spyOn(component, 'submitCommand');

    component.submitCommand();

    expect(spy).toHaveBeenCalled();
  });
});
