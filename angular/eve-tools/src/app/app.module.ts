import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {RoutingModule} from './routing/routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {LayoutComponent} from "./layout/layout.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MaterialModule} from "./material/material.module";
import {FlexLayoutModule} from "@angular/flex-layout";
import {HttpClientModule} from '@angular/common/http';

import {HeaderComponent} from './navigation/header/header.component';
import {SidenavListComponent} from './navigation/sidenav-list/sidenav-list.component';
import {FleetDetailsComponent} from './fleet/fleet-details/fleet-details.component';
import {FleetFinderComponent} from './fleet/fleet-finder/fleet-finder.component';
import {UserLoginComponent} from './user/user-login/user-login.component';

@NgModule({
  declarations: [
    AppComponent,
    LayoutComponent,
    HomeComponent,
    HeaderComponent,
    SidenavListComponent,
    FleetDetailsComponent,
    FleetFinderComponent,
    UserLoginComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MaterialModule,
    FlexLayoutModule,
    RoutingModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
