import { Injectable } from '@angular/core';
import { Http } from '@capacitor-community/http';
import { Device, DeviceInfo } from '@capacitor/device';
import { environment } from 'src/environments/environment';
import { Ticket } from '../domain/ticket';

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
export class Api {
    /**
     * Withraw the next ticket
     * 
     * @param dispenserId The id of a dispenser with an active session
     * 
     * @returns A promise which returns the next ticket, or null if
     * there is no active session for the dispenser, or the tickets
     * are finished, or eventually there is no dispenser with such
     * an id.
     */
    async withraw(dispenserId : number) : Promise<Ticket> {
        return toApiUri("/user/dispenser/" + dispenserId + "/withraw")
            .then(url => Http.get({url: url}))
            .then(response => {
                if (response.status == 200) {
                    return response.data;
                }
                return null;
            })
            .catch(reason => null);
            ;
    }
}