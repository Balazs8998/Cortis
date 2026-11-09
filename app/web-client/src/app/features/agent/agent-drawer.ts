import { Component, EventEmitter, HostListener, OnInit, Output, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { of, switchMap } from 'rxjs';
import { AgentApiService, AgentConversation, AgentMessage, AgentToolEvent } from './agent-api.service';

@Component({
  selector: 'app-cortis-agent-drawer',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './agent-drawer.html',
  styleUrl: './agent-drawer.css'
})
export class AgentDrawerComponent implements OnInit {
  @Output() readonly closed = new EventEmitter<void>();
  private readonly api = inject(AgentApiService);
  private generation = 0;
  readonly conversations = signal<AgentConversation[]>([]);
  readonly selectedId = signal<string | null>(null);
  readonly messages = signal<AgentMessage[]>([]);
  readonly draft = signal('');
  readonly busy = signal(false);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly lastTools = signal<AgentToolEvent[]>([]);
  readonly showConversations = signal(false);
  readonly canSend = computed(() => this.draft().trim().length > 0 && this.draft().length <= 3000 && !this.busy() && !this.loading());

  ngOnInit(): void { this.reloadConversations(); }

  close(): void { this.closed.emit(); }
  @HostListener('document:keydown.escape') onEscape(): void { this.close(); }

  reloadConversations(): void {
    this.loading.set(true);
    this.api.list().subscribe({
      next: conversations => {
        this.conversations.set(conversations);
        this.loading.set(false);
        if (conversations.length > 0 && !this.selectedId()) this.openConversation(conversations[0]!.id);
      },
      error: e => { this.loading.set(false); this.error.set(this.errorText(e)); }
    });
  }

  openConversation(id: string): void {
    if (this.busy()) return;
    const generation = ++this.generation;
    this.selectedId.set(id);
    this.showConversations.set(false);
    this.lastTools.set([]);
    this.loading.set(true);
    this.error.set('');
    this.api.messages(id).subscribe({
      next: messages => {
        if (generation !== this.generation) return;
        this.messages.set(messages);
        this.loading.set(false);
      },
      error: e => {
        if (generation !== this.generation) return;
        this.loading.set(false);
        this.error.set(this.errorText(e));
      }
    });
  }

  newConversation(): void {
    if (this.busy()) return;
    this.generation++;
    this.selectedId.set(null);
    this.messages.set([]);
    this.draft.set('');
    this.lastTools.set([]);
    this.showConversations.set(false);
    this.error.set('');
    this.loading.set(false);
  }

  example(message: string): void { this.draft.set(message); }
  keydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }

  send(): void {
    if (!this.canSend()) return;
    const text = this.draft().trim();
    this.draft.set('');
    this.error.set('');
    this.lastTools.set([]);
    this.busy.set(true);
    this.messages.update(previous => [...previous, { role: 'user', content: text, createdAt: new Date().toISOString() }]);
    const selected = this.selectedId();
    const conversation$ = selected
      ? of({ id: selected, title: '', updatedAt: '' } as AgentConversation)
      : this.api.create();

    conversation$.pipe(switchMap(conversation => {
      this.selectedId.set(conversation.id);
      return this.api.send(conversation.id, text);
    })).subscribe({
      next: response => {
        this.messages.update(previous => [...previous, {
          role: 'assistant', content: response.message, createdAt: new Date().toISOString()
        }]);
        this.lastTools.set(response.tools);
        this.busy.set(false);
        this.api.list().subscribe({ next: rows => this.conversations.set(rows), error: () => { /* chat answer already arrived */ } });
      },
      error: e => {
        const message = this.errorText(e);
        this.busy.set(false);
        if (this.selectedId()) this.openConversation(this.selectedId()!);
        this.error.set(message);
      }
    });
  }

  private errorText(e: unknown): string {
    if (!(e instanceof HttpErrorResponse)) return 'Ismeretlen kommunikációs hiba.';
    if (e.status === 0) return 'A CORTIS backend nem érhető el. Ellenőrizd a proxyt.';
    if (e.status === 503) return 'Az Ollama nem érhető el, vagy a konfigurált modell nem válaszol.';
    if (e.status === 403) return 'Nincs jogosultságod ehhez a művelethez.';
    if (e.status === 409) return 'Ebben a beszélgetésben még fut egy korábbi válasz.';
    if (e.status === 429) return 'Túl sok agentkérés. Próbáld újra később.';
    return `Az agentkérés sikertelen (HTTP ${e.status}).`;
  }
}
