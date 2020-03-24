import {Component, OnDestroy, OnInit} from '@angular/core';
import {BlueprintModel} from '../shared/model/blueprint.model';
import {AssetsService} from '../service/assets.service';
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-blueprint-list',
  templateUrl: './blueprint-list.component.html',
  styleUrls: ['./blueprint-list.component.css']
})
export class BlueprintListComponent implements OnInit, OnDestroy {

  blueprints: BlueprintModel[];
  errorMessage: string;
  private routeListener$: Subscription;

  constructor(
    private route: ActivatedRoute, private assetsService: AssetsService) {
  }

  ngOnInit() {
    this.errorMessage = undefined;
    console.log(this.route.fragment);
    this.routeListener$ = this.route.data
      .subscribe((data: any) => {
          console.log(data);
          if (data.personal) {
            this.assetsService.getPersonalBlueprints().subscribe(
              (blueprintData: BlueprintModel[]) =>
                blueprintData && blueprintData.length > 0
                  ? this.blueprints = blueprintData
                  : this.errorMessage = 'No blueprints to display',
              err => this.errorMessage = err
            );
          } else if (data.corporate) {
            this.assetsService.getCorporateBlueprints().subscribe(
              (blueprintData: BlueprintModel[]) =>
                blueprintData && blueprintData.length > 0
                  ? this.blueprints = blueprintData
                  : this.errorMessage = 'No blueprints to display',
              err => this.errorMessage = err
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

}
