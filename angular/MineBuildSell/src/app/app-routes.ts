import {Routes} from '@angular/router';

import {HomeComponent} from './home/home.component';
import {LoginComponent} from './login/login.component';
import {BlueprintListComponent} from './blueprint-list/blueprint-list.component';
import {BlueprintDetailsComponent} from './blueprint-details/blueprint-details.component';
import {ItemListComponent} from './item-list/item-list.component';
import {ItemDetailsComponent} from './item-details/item-details.component';
import {BlueprintTreeComponent} from './blueprint-tree/blueprint-tree.component';
import {FleetListComponent} from './fleet-list/fleet-list.component';
import {FleetDetailsComponent} from './fleet-details/fleet-details.component';
import {InvitationDetailsComponent} from './invitation-details/invitation-details.component';

export const APP_ROUTES: Routes = [
  {path: '', component: HomeComponent},
  {path: 'home', component: HomeComponent},
  {path: 'login', redirectTo: 'login/', pathMatch: 'full'},
  {path: 'login/:eveState', component: LoginComponent},
  {path: 'blueprints/personal', component: BlueprintListComponent, data: {personal: true}},
  {path: 'blueprints/corporate', component: BlueprintListComponent, data: {corporate: true}},
  {path: 'blueprints/all', component: BlueprintTreeComponent},
  {path: 'blueprint/:type/:id', component: BlueprintDetailsComponent},
  {path: 'items', component: ItemListComponent},
  {path: 'item/:id', component: ItemDetailsComponent},
  {path: 'fleets/all', component: FleetListComponent, data: {owned: false, active: false}},
  {path: 'fleets/all/active', component: FleetListComponent, data: {owned: false, active: true}},
  {path: 'fleets/owned', component: FleetListComponent, data: {owned: true, active: false}},
  {path: 'fleets/owned/active', component: FleetListComponent, data: {owned: true, active: true}},
  {path: 'fleet', component: FleetDetailsComponent},
  {path: 'fleet/:id', component: FleetDetailsComponent},
  {path: 'fleets/invite/:key', component: InvitationDetailsComponent},
  {path: '**', redirectTo: 'home'}
];
