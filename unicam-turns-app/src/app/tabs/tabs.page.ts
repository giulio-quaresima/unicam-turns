import { Component } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-tabs',
  templateUrl: 'tabs.page.html',
  styleUrls: ['tabs.page.scss']
})
export class TabsPage {

  constructor(public oauthService : OAuthService) {
  }

  get authenticated() {
    return this.oauthService.hasValidAccessToken();
  }

  logout() {
    // this.oauthService.revokeTokenAndLogout(); // Currently not supported by my auth server (missing revocation_endpoint)
    this.oauthService.logOut(false);
    this.oauthService.tryLogin();
    // this.oauthService.postLogoutRedirectUri
  }

}
