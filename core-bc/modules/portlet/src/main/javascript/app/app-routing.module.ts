import {NgModule}             from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {View1Component} from './view1/view1.component';
import {IfeedComponent} from "./ifeed/ifeed.component";
import {UserComponent} from "./view/user/user.component";
import {AdminComponent} from "./view/admin/admin.component";
import {AdminGuard} from "./service/admin-guard.service";

const routes: Routes = [
    {
        path: 'user', component: UserComponent,
        children: [
            {path: 'ifeed/:id', component: IfeedComponent},
            {path: 'ifeed/:id/:urlSafeUrl/:ifeedIdHmac', component: IfeedComponent},
            {path: '**', component: View1Component}
        ]
    },
    {
        path: 'admin',
        component: AdminComponent,
        canActivate: [AdminGuard]
    },
    {path: '**', component: UserComponent}

];
@NgModule({
    imports: [RouterModule.forRoot(routes, {useHash: true})],
    exports: [RouterModule]
})
export class AppRoutingModule {
}