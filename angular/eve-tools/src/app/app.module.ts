import {BrowserModule} from '@angular/platform-browser';
import {LOCALE_ID, NgModule} from '@angular/core';

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
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { SubHeaderComponent } from './sub-header/sub-header.component';
import { RoleDialogComponent } from './fleet/role-dialog/role-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    LayoutComponent,
    HomeComponent,
    HeaderComponent,
    SidenavListComponent,
    FleetDetailsComponent,
    FleetFinderComponent,
    UserLoginComponent,
    SubHeaderComponent,
    RoleDialogComponent,
  ],
    imports: [
        BrowserModule,
        HttpClientModule,
        BrowserAnimationsModule,
        MaterialModule,
        FlexLayoutModule,
        RoutingModule,
        ReactiveFormsModule,
        FormsModule,
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
