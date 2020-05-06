import {Component, OnDestroy, OnInit} from '@angular/core';
import {BlueprintModel} from '../shared/model/blueprint.model';
import {AssetsService} from '../service/assets.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {LoginComponent} from '../login/login.component';

@Component({
  selector: 'app-blueprint-list',
  templateUrl: './blueprint-list.component.html',
  styleUrls: ['./blueprint-list.component.css']
})
export class BlueprintListComponent implements OnInit, OnDestroy {

  blueprints: BlueprintModel[];
  displayBlueprints: BlueprintModel[];
  errorMessage: string;
  bpType: string;
  private routeListener$: Subscription;
  search: string;

  constructor(
    private route: ActivatedRoute, private assetsService: AssetsService, private router: Router, private login: LoginComponent) {
  }

  ngOnInit() {
    this.errorMessage = undefined;
    this.routeListener$ = this.route.data
      .subscribe((data: any) => {
          if (data.personal) {
            this.bpType = 'personal';
            this.assetsService.getPersonalBlueprints().subscribe(
              (blueprintData: BlueprintModel[]) => {
                if (blueprintData && blueprintData.length > 0) {
                  this.blueprints = blueprintData;
                  this.displayBlueprints = blueprintData;
                } else {
                  this.errorMessage = 'No blueprints to display';
                }
              },
              err => {
                this.errorMessage = err;
                if (err.status === 401) {
                  localStorage.setItem('currentPage', '/blueprints/personal');
                  this.router.navigateByUrl('/login');
                }
              }
            );
          } else if (data.corporate && this.login.user.hasRole('director')) {
            this.bpType = 'corporate';
            this.assetsService.getCorporateBlueprints().subscribe(
              (blueprintData: BlueprintModel[]) => {
                if (blueprintData && blueprintData.length > 0) {
                  this.blueprints = blueprintData;
                  this.displayBlueprints = blueprintData;
                } else {
                  this.errorMessage = 'No blueprints to display';
                }
              },
              err => {
                this.errorMessage = err;
                if (err.status === 401) {
                  localStorage.setItem('currentPage', '/blueprints/corporate');
                  this.router.navigateByUrl('/login');
                }
              }
            );
          }
        }
      );
  }

  ngOnDestroy(): void {
    this.routeListener$.unsubscribe();
  }

  blueprintClass(blueprint: BlueprintModel) {
    return {
      'list-group-item': true,
      'list-group-item-action': true,
    };
  }

  searchBp() {
    this.displayBlueprints = this.blueprints.filter(bp => bp.name.toLowerCase().includes(this.search.toLowerCase()));
  }
}
