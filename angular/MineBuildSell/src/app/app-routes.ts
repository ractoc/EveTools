import {Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {LoginComponent} from "./login/login.component";

export const APP_ROUTES: Routes = [
  {path: '', component: HomeComponent},
  {path: 'home', component: HomeComponent},
  {path: 'login', redirectTo: 'login/', pathMatch: 'full'},
  {path: 'login/:eveState', component: LoginComponent},
  {path: '**', redirectTo: 'home'}
];
