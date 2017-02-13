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

    putWithSuper(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
        return super.put(url, body, options);
    }

    put(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
        let self = this;
        return Observable.create((observer: Observer<Response>) => {
            try {
                let token: string = <string> self.authConfig.getConfig().tokenGetter();
                let decodeToken = self.jwtHelper.decodeToken(token);

                if (self.isExpired(decodeToken)) {
                    // Expired -> renew
                    console.log('JWT has expired. Renew...');
                    self.renewJwtAndMakePut(self, url, body, options, observer);
                } else {
                    console.log('Using still valid JWT');
                    self.makeThePut(self, url, body, options, observer);
                }

            } catch (e) {
                console.log(e);
                self.renewJwtAndMakePut(self, url, body, options, observer);
            }
        });
    }

    private isExpired(decodeToken: any) {
        return decodeToken.exp - new Date().getTime() / 1000 < 0;
    }

    private renewJwtAndMakePut(self: RefreshTokenAuthHttp,
                               url: string,
                               body: any,
                               options: RequestOptionsArgs,
                               observer: Observer<Response>) {
        self.baseHttp.get(self.ifeedService.resourceUrl + "&p_p_resource_id=refreshToken")
            .map((response: Response) => response.text())
            .subscribe(
                (json: string) => {
                    console.log('Setting new JWT...');
                    sessionStorage.setItem("jwtToken", json);
                    // Continue with the request which was the purpose of the originating call
                    self.makeThePut(self, url, body, options, observer);
                },
                (err: Error) => {
                    observer.error(err);
                    observer.complete();
                }
            );
    }

    private makeThePut(self: RefreshTokenAuthHttp,
                       url: string,
                       body: any,
                       options: RequestOptionsArgs,
                       observer: Observer<Response>) {
        let observableResponse: Observable<Response> = self.putWithSuper(url, body, options);
        observableResponse.subscribe(response => {
            observer.next(response);
            observer.complete();
        }, (err: Error) => {
            observer.error(err);
            observer.complete();
        });
    }

    get(url: string, options?: RequestOptionsArgs): Observable<Response> {
        // Get requests are performed without any JWT features. We know the server-side doesn't require JWTs for get
        // requests.
        return this.baseHttp.get(url, options);
    }
}