import {AuthHttp, AuthConfig, JwtHelper} from "angular2-jwt";
import {RequestOptionsArgs, Response, Http, RequestOptions} from "@angular/http";
import {Observable} from "rxjs";
import {GlobalStateService} from "./global-state.service";

export class RefreshTokenAuthHttp extends AuthHttp {

    private authConfig: AuthConfig;
    private baseHttp: Http;

    constructor(options: AuthConfig,
                http: Http,
                defOpts: RequestOptions,
                private jwtHelper: JwtHelper,
                private globalStateService: GlobalStateService) {
        super(options, http, defOpts);
        this.authConfig = options;
        this.baseHttp = http;
    }

    put(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
        let token: string = <string> this.authConfig.getConfig().tokenGetter();
        let decodeToken = this.jwtHelper.decodeToken(token);

        if (this.isExpired(decodeToken)) {
            // Expired -> renew
            console.log('JWT has expired. Renew...');
            return this.baseHttp.get(this.globalStateService.resourceUrl + "&p_p_resource_id=refreshToken")
                .map((response: Response) => response.text())
                .flatMap(jwtToken => {
                    sessionStorage.setItem("jwtToken", jwtToken);
                    // Continue with the request which was the purpose of the originating call
                    // self.makeThePut(self, url, body, options, observer);
                    return super.put(url, body, options);
                })
        } else {
            console.log('Using still valid JWT');
            return super.put(url, body, options);
        }
    }

    private isExpired(decodeToken: any) {
        return decodeToken.exp - new Date().getTime() / 1000 < 0;
    }

    get(url: string, options?: RequestOptionsArgs): Observable<Response> {
        // Get requests are performed without any JWT features. We know the server-side doesn't require JWTs for get
        // requests.
        return this.baseHttp.get(url, options);
    }
}