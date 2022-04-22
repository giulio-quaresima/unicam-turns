import { Injectable } from '@angular/core';
import { Http, HttpHeaders, HttpOptions } from '@capacitor-community/http';
import { OAuthStorage } from 'angular-oauth2-oidc';
import { Device, DeviceInfo } from '@capacitor/device';
import { environment } from 'src/environments/environment';
import { Ticket } from '../domain/ticket';
import { HttpResponse } from '@capacitor-community/http';

const getDeviceInfo = async () => {
    return Device.getInfo();
};

const getApiBaseUri = async () => {
    let host : string;
    // DeviceInfo.isVirtual è true quando si usa l'emulatore android
    if (!environment.production && (await getDeviceInfo()).isVirtual) {
        host = "10.0.2.2"; // 10.0.2.2 è il localhost dell'host quando si usa l'emulatore android, vedi https://developer.android.com/studio/run/emulator-networking
    } else {
        host = environment.apiInfo.host;
    }
    return environment.apiInfo.protocol + "://" + host + ":" + environment.apiInfo.port;
}
  
const toApiUri = async (path:string) => {
    return (await getApiBaseUri()) + path;
}

@Injectable({ providedIn: 'root' })
export class Rest {

    constructor(private authStorage: OAuthStorage) {}

    get(httpOptions : HttpOptions) : Promise<HttpResponse> {
        return this.decorate(httpOptions).then(opts => Http.get(opts));
    }

    post(httpOptions : HttpOptions) : Promise<HttpResponse> {
        return this.decorate(httpOptions).then(opts => Http.post(opts));
    }

    put(httpOptions : HttpOptions) : Promise<HttpResponse> {
        return this.decorate(httpOptions).then(opts => Http.put(opts));
    }

    delete(httpOptions : HttpOptions) : Promise<HttpResponse> {
        return this.decorate(httpOptions).then(opts => Http.del(opts));
    }

    async decorate(httpOptions : HttpOptions) : Promise<HttpOptions> {
        let httpHeaders : HttpHeaders = {
            "Content-Type" : "application/json"
        };

        let token = this.authStorage.getItem('access_token');
        if (!!token) {
            httpHeaders["Authorization"] = "Bearer " + token;
        }
        
        httpOptions.headers = {...httpHeaders, ...httpOptions.headers};
        httpOptions.url = (await toApiUri(httpOptions.url));
        
        return httpOptions;
    }

}
