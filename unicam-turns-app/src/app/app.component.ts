import { Component } from '@angular/core';
import { filter } from 'rxjs/operators';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './auth.config';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {

  constructor(private oauthService: OAuthService) {
    this.configureOAuth();
  }

  private configureOAuth() {
    this.oauthService.configure(authConfig);
    // this.oauthService.tokenValidationHandler = new JwksValidationHandler();

    this.oauthService.loadDiscoveryDocumentAndLogin(
      {
        customHashFragment: window.location.search // https://github.com/manfredsteyer/angular-oauth2-oidc/issues/1023#issuecomment-780063508
      }
    ).then((_) => {
      // if (useHash) {
      //   this.router.navigate(['/']);
      // }
      console.log(_);
    });

    // Optional
    this.oauthService.setupAutomaticSilentRefresh();

    // Display all events
    this.oauthService.events.subscribe((e) => {
      // tslint:disable-next-line:no-console
      console.debug('oauth/oidc event', e);
    });

    this.oauthService.events
      .pipe(filter((e) => e.type === 'session_terminated'))
      .subscribe((e) => {
        // tslint:disable-next-line:no-console
        console.debug('Your session has been terminated!');
      });
  }

}
