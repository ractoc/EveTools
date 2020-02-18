import {Component, OnDestroy, OnInit} from '@angular/core';
import {AssetsService} from "../service/assets.service";
import {BlueprintModel} from "../shared/model/blueprint.model";
import {Subscription} from "rxjs";
import {ActivatedRoute} from "@angular/router";
import {CalculatorService} from "../service/calculator.service";

@Component({
  selector: 'app-blueprint-details',
  templateUrl: './blueprint-details.component.html',
  styleUrls: ['./blueprint-details.component.css']
})
export class BlueprintDetailsComponent implements OnInit, OnDestroy {

  public bp: BlueprintModel;
  private routeListener$: Subscription;
  public isCalculateCollapsed = true;
  public isCalculating = false;

  constructor(private assetsService: AssetsService, private calculatorService: CalculatorService, private route: ActivatedRoute) {
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

  calculate() {
    this.isCalculating = true;
    this.calculatorService.calculateBlueprint(this.bp).subscribe(
      (blueprintData: BlueprintModel) => {
        this.bp = blueprintData;
        console.log("calculated blueprint", blueprintData);
        this.isCalculating = false;
      });
  }

  calculateClass() {
    return {
      'btn': true,
      'btn-outline-info': true,
    };
  }

  calculateButtonText() {
    if (this.isCalculating) {
      return 'Calculating...';
    }
    return 'Calculate';
  }
}
