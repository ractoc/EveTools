import {Routes} from '@angular/router';

import {HomeComponent} from './home/home.component';
import {LoginComponent} from './login/login.component';
import {BlueprintListComponent} from './blueprint-list/blueprint-list.component';
import {BlueprintDetailsComponent} from './blueprint-details/blueprint-details.component';
import {ItemListComponent} from './item-list/item-list.component';
import {ItemDetailsComponent} from './item-details/item-details.component';
import {BlueprintTreeComponent} from './blueprint-tree/blueprint-tree.component';

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
  {path: '**', redirectTo: 'home'}
];
