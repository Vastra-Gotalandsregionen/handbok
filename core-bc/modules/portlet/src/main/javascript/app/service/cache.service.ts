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
            .toArray()
            .flatMap((keys: string[]) => {
                // Return the cache or null. TODO make sure wrong cache isn't returned, if there are multiple caches containing 'handbok'.
                return keys.length > 0 ? Observable.fromPromise(caches.open(keys[0])) : Observable.of(null);
                // Return the cache or a dummy cache. TODO make sure wrong cache isn't returned, if there are multiple caches containing 'handbok'.
                // return keys.length > 0 ? Observable.fromPromise(caches.open(keys[0])) : Observable.of(new Cache());
            })
            // .first()
            // .flatMap(key => Observable.fromPromise(caches.open(key)));
    }

    notifyCacheStatusUpdate(ifeedId: string) {
        console.log('notify cache status update: ' + ifeedId);
        this.cacheStatusEvent$.next(ifeedId);
    }
}
