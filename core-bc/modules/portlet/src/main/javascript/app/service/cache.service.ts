import {Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";

@Injectable()
export class CacheService {

    public cacheStatusEvent$: Subject<string> = new Subject<string>();

    getCache(): Observable<Cache> {
        let keysPromise = <Promise<string[]>> caches.keys();
        return Observable.fromPromise(keysPromise)
            .concatMap(x => x)
            .filter((key: string) => key.indexOf('handbok') > -1)
            .first()
            .flatMap(key => Observable.fromPromise(caches.open(key)));
    }

    notifyCacheStatusUpdate(ifeedId: string) {
        console.log('notify cache status update: ' + ifeedId);
        this.cacheStatusEvent$.next(ifeedId);
    }
}
