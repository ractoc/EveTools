import {Component, OnDestroy, OnInit} from '@angular/core';
import {AssetsService} from "../service/assets.service";
import {BlueprintModel} from "../shared/model/blueprint.model";
import {Subscription} from "rxjs";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-blueprint-details',
  templateUrl: './blueprint-details.component.html',
  styleUrls: ['./blueprint-details.component.css']
})
export class BlueprintDetailsComponent implements OnInit, OnDestroy {

  bp: BlueprintModel;
  private routeListener$: Subscription;

  constructor(private assetsService: AssetsService, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
          if (params.id) {
            this.bp = this.assetsService.getBlueprint(params.id);
          } else {
            this.bp = undefined;
          }
        }
      );
  }

  ngOnDestroy(): void {
    this.routeListener$.unsubscribe();
  }

}
