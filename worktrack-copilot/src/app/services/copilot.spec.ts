import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CopilotService } from './copilot';
import { firstValueFrom } from 'rxjs';

describe('CopilotService', () => {
  let service: CopilotService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CopilotService]
    });

    service = TestBed.inject(CopilotService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should send a command and return the AI response as a string', async () => {
    const testPrompt = 'What is employee WT-434019 currently working on?';
    const mockResponse = 'Employee WT-434019 is currently assigned to the mobile app redesign project.';

    const promise = firstValueFrom(service.sendCommand(testPrompt));

    const req = httpMock.expectOne('/api/ai/command');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBe(testPrompt);
    expect(req.request.responseType).toBe('text');

    req.flush(mockResponse);

    const response = await promise;
    expect(response).toBe(mockResponse);
  });

  it('should send raw text as request body, not a JSON object', async () => {
    const testPrompt = 'Status update for all pending tasks';

    const promise = firstValueFrom(service.sendCommand(testPrompt));

    const req = httpMock.expectOne('/api/ai/command');
    expect(typeof req.request.body).toBe('string');
    expect(req.request.body).toBe(testPrompt);
    expect(req.request.body).not.toEqual({ prompt: testPrompt });

    req.flush('Mock response');
    await promise;
  });

  it('should propagate HTTP 500 error when backend is unavailable', async () => {
    const testPrompt = 'Any query';

    const promise = firstValueFrom(service.sendCommand(testPrompt));

    const req = httpMock.expectOne('/api/ai/command');
    req.flush('Internal Server Error', { status: 500, statusText: 'Internal Server Error' });

    await expect(promise).rejects.toMatchObject({ status: 500, error: 'Internal Server Error' });
  });

  it('should propagate HTTP 404 error when endpoint is not found', async () => {
    const testPrompt = 'Any query';

    const promise = firstValueFrom(service.sendCommand(testPrompt));

    const req = httpMock.expectOne('/api/ai/command');
    req.flush('Not Found', { status: 404, statusText: 'Not Found' });

    await expect(promise).rejects.toMatchObject({ status: 404 });
  });

  it('should send empty string prompt without modification', async () => {
    const testPrompt = '';

    const promise = firstValueFrom(service.sendCommand(testPrompt));

    const req = httpMock.expectOne('/api/ai/command');
    expect(req.request.body).toBe('');

    req.flush('Mock response');
    await promise;
  });

  it('should have responseType set to "text"', async () => {
    const testPrompt = 'Any query';

    const promise = firstValueFrom(service.sendCommand(testPrompt));

    const req = httpMock.expectOne('/api/ai/command');
    expect(req.request.responseType).toBe('text');

    req.flush('Plain text response');
    await promise;
  });
});
