import {NgModule}             from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {IfeedComponent} from "./view/user/ifeed/ifeed.component";
import {UserComponent} from "./view/user/user.component";
import {AdminComponent} from "./view/admin/admin.component";
import {AdminGuard} from "./service/admin-guard.service";
import {EditComponent} from "./view/edit/edit.component";
import {EditGuard} from "./service/edit-guard.service";

const routes: Routes = [
    {
        path: 'user', component: UserComponent,
        children: [
            {path: 'ifeed/:id', component: IfeedComponent},
            {path: 'ifeed/:id/:urlSafeUrl/:ifeedIdHmac', component: IfeedComponent},
            {path: '**', component: UserComponent}
        ]
    },
    {
        path: 'admin',
        component: AdminComponent,
        canActivate: [AdminGuard]
    },
    {
        path: 'edit',
        component: EditComponent,
        canActivate: [EditGuard]
    },
    {path: '**', component: UserComponent}

];
@NgModule({
    imports: [RouterModule.forRoot(routes, {useHash: true})],
    exports: [RouterModule]
})
export class AppRoutingModule {
}