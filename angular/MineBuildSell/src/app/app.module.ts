import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {RouterModule} from '@angular/router';

import {APP_ROUTES} from './app-routes';
import {MainComponent} from './main/main.component';
import {HomeComponent} from './home/home.component';
import {LoginComponent} from './login/login.component';
import {HttpClientModule} from '@angular/common/http';
import {BlueprintListComponent} from './blueprint-list/blueprint-list.component';
import {BlueprintDetailsComponent} from './blueprint-details/blueprint-details.component';

@NgModule({
  declarations: [
    MainComponent,
    HomeComponent,
    LoginComponent,
    BlueprintListComponent,
    BlueprintDetailsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    RouterModule,
    RouterModule.forRoot(APP_ROUTES)
  ],
  providers: [],
  bootstrap: [MainComponent]
})
export class AppModule { }
