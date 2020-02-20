import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FormBuilder, FormGroup} from '@angular/forms';

import {Subscription} from "rxjs";

import {AssetsService} from "../service/assets.service";
import {CalculatorService} from "../service/calculator.service";
import {UniverseService} from "../service/universe.service";

import {BlueprintModel} from "../shared/model/blueprint.model";
import {MarketHubModel} from "../shared/model/markethub.model";

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
  public isCalculated = false;

  public form: FormGroup;
  public marketHubs: MarketHubModel[];
  public errorMessage: string;

  constructor(private assetsService: AssetsService,
              private universeService: UniverseService,
              private calculatorService: CalculatorService,
              private route: ActivatedRoute,
              private formBuilder: FormBuilder
  ) {
    this.form = this.formBuilder.group({
      marketHubs: [''],
      nrRuns: 1
    });
  }

  ngOnInit() {
    this.errorMessage = undefined;
    this.isCalculated = false;
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
          if (params.id) {
            this.bp = this.assetsService.getBlueprint(params.id);
          } else {
            this.bp = undefined;
          }
        }
      );
    this.universeService.getMarketHubs().subscribe(
      (marketHubData: MarketHubModel[]) => {
        if (marketHubData && marketHubData.length > 0) {
          this.marketHubs = marketHubData
          this.form.controls.marketHubs.patchValue(this.marketHubs[0].id);
        } else {
          this.errorMessage = 'No market hubs to display'
        }
      },
      err => this.errorMessage = err
    );
  }

  ngOnDestroy(): void {
    this.routeListener$.unsubscribe();
  }

  calculate(blueprint: BlueprintModel) {
    const marketHub: MarketHubModel = this.marketHubs.find((mh) => mh.id == this.form.value.marketHubs);
    console.log("marketHub:", marketHub);
    const nrRuns: number = this.form.value.nrRuns;

    this.isCalculating = true;
    this.isCalculated = false;
    this.calculatorService.calculateBlueprint(this.bp, marketHub, nrRuns).subscribe(
      (blueprintData: BlueprintModel) => {
        this.bp = blueprintData;
        console.log("calculated blueprint", blueprintData);
        this.isCalculating = false;
        this.isCalculated = true;
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
