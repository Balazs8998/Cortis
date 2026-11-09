import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiTranslationService } from '../../shared/i18n/ui-translation.service';

export interface AgentConversation {
  id: string;
  title: string;
  updatedAt: string;
}
export interface AgentMessage {
  role: 'user' | 'assistant';
  content: string;
  createdAt: string;
}
export interface AgentToolEvent {
  name: string;
  status: string;
  note: string;
}
export interface AgentAnswer {
  message: string;
  iterations: number;
  toolCalls: number;
  terminationReason: string;
  tools: AgentToolEvent[];
}

@Injectable({ providedIn: 'root' })
export class AgentApiService {
  private readonly http = inject(HttpClient);
  private readonly locale = inject(UiTranslationService);
  private readonly base = '/api/agent/conversations';

  list(): Observable<AgentConversation[]> { return this.http.get<AgentConversation[]>(this.base); }
  create(): Observable<AgentConversation> { return this.http.post<AgentConversation>(this.base, {}); }
  messages(id: string): Observable<AgentMessage[]> {
    return this.http.get<AgentMessage[]>(`${this.base}/${encodeURIComponent(id)}/messages`);
  }
  send(id: string, message: string): Observable<AgentAnswer> {
    return this.http.post<AgentAnswer>(`${this.base}/${encodeURIComponent(id)}/messages`, { message }, {
      headers: { 'Accept-Language': this.locale.language() }
    });
  }
}
