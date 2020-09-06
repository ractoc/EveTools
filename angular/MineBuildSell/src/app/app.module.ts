import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {APP_ROUTES} from './app-routes';
import {MainComponent} from './main/main.component';
import {HomeComponent} from './home/home.component';
import {LoginComponent} from './login/login.component';
import {HttpClientModule} from '@angular/common/http';
import {BlueprintListComponent} from './blueprint-list/blueprint-list.component';
import {BlueprintDetailsComponent} from './blueprint-details/blueprint-details.component';
import {ItemListComponent} from './item-list/item-list.component';
import {ItemDetailsComponent} from './item-details/item-details.component';
import {MarketGroupTreeComponent} from './market-group-tree/market-group-tree.component';
import {BlueprintTreeComponent} from './blueprint-tree/blueprint-tree.component';
import {FleetListComponent} from './fleet-list/fleet-list.component';
import {FleetDetailsComponent} from './fleet-details/fleet-details.component';

@NgModule({
  declarations: [
    MainComponent,
    HomeComponent,
    LoginComponent,
    BlueprintListComponent,
    BlueprintDetailsComponent,
    ItemListComponent,
    ItemDetailsComponent,
    MarketGroupTreeComponent,
    BlueprintTreeComponent,
    FleetListComponent,
    FleetDetailsComponent
  ],
  imports: [
    NgbModule,
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    RouterModule,
    RouterModule.forRoot(APP_ROUTES),
    FormsModule
  ],
  providers: [LoginComponent],
  bootstrap: [MainComponent]
})
export class AppModule { }
