import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from "../home/home.component";
import {FleetFinderComponent} from "../fleet/fleet-finder/fleet-finder.component";
import {FleetDetailsComponent} from "../fleet/fleet-details/fleet-details.component";
import {UserLoginComponent} from "../user/user-login/user-login.component";


const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: 'user', redirectTo: 'user/login/', pathMatch: 'full'},
  {path: 'user/login', redirectTo: 'user/login/', pathMatch: 'full'},
  {path: 'user/login/:eveState', component: UserLoginComponent},
  {path: 'fleet', redirectTo: 'fleet/find', pathMatch: 'full'},
  {path: 'fleet/new', component: FleetDetailsComponent},
  {path: 'fleet/find', component: FleetFinderComponent},
  {path: 'fleet/details/:id', component: FleetDetailsComponent},
  {path: 'fleet/register/:key', component: FleetDetailsComponent},
  {path: '', redirectTo: '/home', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class RoutingModule {
}
