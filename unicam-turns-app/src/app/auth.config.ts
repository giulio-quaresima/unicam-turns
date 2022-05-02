// This api will come in the next version

import { AuthConfig } from 'angular-oauth2-oidc';
import { environment } from 'src/environments/environment';

export const authConfig: AuthConfig = {
  // Url of the Identity Provider
  issuer: environment.oauthConfig.issuer,
  // issuer: 'https://demo.c2id.com',

  logoutUrl: environment.oauthConfig.issuer + "/logout",

  requireHttps: false,

  // URL of the SPA to redirect the user to after login
  // redirectUri: window.location.origin
  //   + ((localStorage.getItem('useHashLocationStrategy') === 'true')
  //     ? '/#/index.html'
  //     : '/index.html'),

  redirectUri: window.location.origin,

  // URL of the SPA to redirect the user after silent refresh
//   silentRefreshRedirectUri: window.location.origin + '/silent-refresh.html',
  silentRefreshRedirectUri: window.location.origin,
  // silentRefreshRedirectUri: 'http://127.0.0.1:8100/index.html',
  // The SPA's id. The SPA is registerd with this id at the auth-server
  clientId: 'unicam-turns-app',
  // clientId: '000123',

  responseType: 'code',

  // set the scope for the permissions the client should request
  // The first three are defined by OIDC. The 4th is a usecase-specific one
  scope: 'openid profile email',
  
  /*
  Per attivare il refresh token (probabilmente inutile)
  Please also note, that you have to request the offline_access scope to get a refresh token.
  https://manfredsteyer.github.io/angular-oauth2-oidc/docs/additional-documentation/refreshing-a-token.html
  */
  // scope: 'openid profile email offline_access', 

  // silentRefreshShowIFrame: true,

  showDebugInformation: true,

  sessionChecksEnabled: true,

  // timeoutFactor: 0.01,
};