import {Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {LoginComponent} from './login/login.component';
import {BlueprintListComponent} from "./blueprint-list/blueprint-list.component";
import {BlueprintDetailsComponent} from "./blueprint-details/blueprint-details.component";

export const APP_ROUTES: Routes = [
  {path: '', component: HomeComponent},
  {path: 'home', component: HomeComponent},
  {path: 'login', redirectTo: 'login/', pathMatch: 'full'},
  {path: 'login/:eveState', component: LoginComponent},
  {path: 'blueprints', component: BlueprintListComponent},
  {path: 'blueprint/:id', component: BlueprintDetailsComponent},
  {path: '**', redirectTo: 'home'}
];
