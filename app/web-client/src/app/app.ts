import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  username = signal('');
  password = signal('');
  passwordVisible = signal(false);
  rememberMe = signal(false);

  isLoading = signal(false);
  loginError = signal<string | null>(null);

  onUsernameInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.username.set(input.value);
  }

  onPasswordInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.password.set(input.value);
  }

  showPassword(): void {
    this.passwordVisible.set(true);
  }

  hidePassword(): void {
    this.passwordVisible.set(false);
  }

  onRememberMeChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.rememberMe.set(input.checked);
  }

  login(): void {
    this.loginError.set(null);

    if (!this.username() || !this.password()) {
      this.loginError.set('Felhasználónév és jelszó megadása kötelező.');
      return;
    }

    this.isLoading.set(true);

    console.log('Login attempt:');
    console.log('Username:', this.username());
    console.log('Password:', this.password());
    console.log('Remember me:', this.rememberMe());

    setTimeout(() => {
      this.isLoading.set(false);
      this.loginError.set('Backend még nincs rákötve. Ez most csak frontend próba.');
    }, 800);
  }

  forgotPassword(): void {
    console.log('Forgot password clicked');
    this.loginError.set('Jelszó-visszaállítás még nincs implementálva.');
  }
}
