// This api will come in the next version

import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  // Url of the Identity Provider
  issuer: 'http://localhost:9000',

  requireHttps: false,

  // URL of the SPA to redirect the user to after login
  // redirectUri: window.location.origin
  //   + ((localStorage.getItem('useHashLocationStrategy') === 'true')
  //     ? '/#/index.html'
  //     : '/index.html'),

  redirectUri: window.location.origin + '/index.html',

  // URL of the SPA to redirect the user after silent refresh
//   silentRefreshRedirectUri: window.location.origin + '/silent-refresh.html',
  silentRefreshRedirectUri: window.location.origin + '/index.html',
  // silentRefreshRedirectUri: 'http://127.0.0.1:8100/index.html',
  // The SPA's id. The SPA is registerd with this id at the auth-server
  clientId: 'unicam-turns-app',

  responseType: 'code',

  // set the scope for the permissions the client should request
  // The first three are defined by OIDC. The 4th is a usecase-specific one
  // scope: 'openid profile email',
  scope: 'openid',

  // silentRefreshShowIFrame: true,

  showDebugInformation: true,

  sessionChecksEnabled: true,

  // timeoutFactor: 0.01,
};