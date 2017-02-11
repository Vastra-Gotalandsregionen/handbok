import {AuthHttp, AuthConfig, JwtHelper} from "angular2-jwt";
import {RequestOptionsArgs, Response, Http, RequestOptions} from "@angular/http";
import {Observable, Observer} from "rxjs";
import {IfeedService} from "./ifeed.service";
export class RefreshTokenAuthHttp extends AuthHttp {

    private authConfig: AuthConfig;
    private baseHttp: Http;

    constructor(options: AuthConfig,
                http: Http,
                defOpts: RequestOptions,
                private jwtHelper: JwtHelper,
                private ifeedService: IfeedService) {
        super(options, http, defOpts);
        this.authConfig = options;
        this.baseHttp = http;
    }

    putWithParent(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
        return super.put(url, body, options);
    }

    put(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
        let self = this;
        return Observable.create((observer: Observer<Response>) => {
            try {
                let token: string = <string> self.authConfig.getConfig().tokenGetter();
                let decodeToken = self.jwtHelper.decodeToken(token);

                if (decodeToken.exp - new Date().getTime() / 1000 < 0) {
                    // Expired -> renew
                    console.log('JWT has expired. Renew...');
                    self.baseHttp.get(self.ifeedService.resourceUrl + "&p_p_resource_id=refreshToken")
                        .map((response: Response) => response.text())
                        .subscribe(
                            (json: string) => {
                                console.log('Setting new JWT...');
                                sessionStorage.setItem("jwtToken", json);
                                // Continue with the request which was the purpose of the originating call
                                let result: Observable<Response> = self.putWithParent(url, body, options);
                                result.subscribe(response => {
                                    observer.next(response);
                                    observer.complete();
                                }, (err: Error) => {
                                    observer.error(err);
                                    observer.complete();
                                });
                            },
                            (err: Error) => {
                                observer.error(err);
                                observer.complete();
                            }
                        );
                } else {
                    console.log('Using still valid JWT');
                    let result: Observable<Response> = self.putWithParent(url, body, options);
                    result.subscribe(response => {
                        observer.next(response);
                        observer.complete();
                    }, (err: Error) => {
                        observer.error(err);
                        observer.complete();
                    });
                }

            } catch (e) {
                self.baseHttp.get(self.ifeedService.resourceUrl + "&p_p_resource_id=refreshToken")
                    .map((response: Response) => response.text())
                    .subscribe(
                        (json: string) => {
                            sessionStorage.setItem("jwtToken", json);
                            // Continue with the request which was the purpose of the originating call
                            let result: Observable<Response> = self.putWithParent(url, body, options);
                            result.subscribe(response => {
                                observer.next(response);
                                observer.complete();
                            }, (err: Error) => {
                                observer.error(err);
                                observer.complete();
                            });
                        },
                        (err: Error) => {
                            observer.error(err);
                            observer.complete();
                        }
                    );
            }
        });
    }

    get(url: string, options?: RequestOptionsArgs): Observable<Response> {
        return this.baseHttp.get(url, options);
    }
}