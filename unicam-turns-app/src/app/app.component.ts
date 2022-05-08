import { Component } from '@angular/core';
import { filter } from 'rxjs/operators';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './auth.config';
import { Router } from '@angular/router';
import { Firebase } from './service/firebase';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {

  constructor(
    private oauthService: OAuthService, 
    private router : Router,
    private firebase : Firebase) {
    this.configureOAuth();
  }

  private configureOAuth() {
    this.oauthService.configure(authConfig);
    // this.oauthService.tokenValidationHandler = new JwksValidationHandler();
    
    console.log('loadDiscoveryDocumentAndLogin(state : "' + window.location.pathname + '");');
    this.oauthService.loadDiscoveryDocumentAndLogin(
      {
        customHashFragment: window.location.search, // https://github.com/manfredsteyer/angular-oauth2-oidc/issues/1023#issuecomment-780063508
        state : window.location.pathname
      }
    ).then((_) => {
      // if (useHash) {
      //   this.router.navigate(['/']);
      // }
      console.log(_);
    });

    // Optional
    // this.oauthService.setupAutomaticSilentRefresh();

    // Display all events
    this.oauthService.events.subscribe((e) => {
      // tslint:disable-next-line:no-console
      console.debug('oauth/oidc event', e);
    });

    this.oauthService.events
      //.pipe(filter((e) => e.type === 'session_terminated'))
      .subscribe((event) => {
        console.log('Received oauth event: ' + event.type);
        if (event.type === 'token_received') {
          console.log("Saved state", this.oauthService.state);
          if (! this.firebase.initialized) {
            console.log("Trying to initialize Firebase");
            this.firebase.initialize();
          }
          if (this.oauthService.state) {
            const redirect_uri = decodeURIComponent(this.oauthService.state);
            console.log("Saved state (decoded)", redirect_uri);
            if (redirect_uri && redirect_uri !== '/') {
              this.oauthService.state = '/'; // Reset saved state
              console.log('redirecting to initially requested page', redirect_uri);
              this.router.navigateByUrl(redirect_uri);
            }
          }
        }
        if (event.type === 'discovery_document_loaded') {
          if (! this.firebase.initialized) {
            console.log("Trying to initialize Firebase");
            this.firebase.initialize();
          }
        }
        if (event.type === 'session_terminated') {
          console.debug('Your session has been terminated!');
        }
      });
  }

}
