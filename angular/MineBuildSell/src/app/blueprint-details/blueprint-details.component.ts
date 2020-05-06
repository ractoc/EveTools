import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup} from '@angular/forms';

import {Subscription} from 'rxjs';

import {AssetsService} from '../service/assets.service';
import {CalculatorService} from '../service/calculator.service';
import {UniverseService} from '../service/universe.service';

import {BlueprintModel} from '../shared/model/blueprint.model';
import {MarketHubModel} from '../shared/model/markethub.model';

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
              private router: Router,
              private formBuilder: FormBuilder
  ) {
    this.form = this.formBuilder.group({
      buyMarketHubs: [],
      sellMarketHubs: [],
      nrRuns: 1
    });
  }

  ngOnInit() {
    this.errorMessage = undefined;
    this.isCalculated = false;
    this.routeListener$ = this.route.params
      .subscribe((params: any) => {
          if (params.id) {
            this.assetsService.getBlueprint(params.id, params.type).subscribe(
              (blueprintData: BlueprintModel) => {
                this.bp = blueprintData;
              },
              err => {
                if (err.status === 401) {
                  localStorage.setItem('currentPage', '/blueprint/' + params.type + '/' + params.id);
                  this.router.navigateByUrl('/login');
                } else {
                  this.errorMessage = 'Unable to load blueprint';
                }
              }
            );
          } else {
            this.bp = undefined;
          }
        }
      );
    this.universeService.getMarketHubs().subscribe(
      (marketHubData: MarketHubModel[]) => {
        if (marketHubData && marketHubData.length > 0) {
          this.marketHubs = marketHubData;
          this.form.controls.buyMarketHubs.patchValue(this.marketHubs[0].id);
          this.form.controls.sellMarketHubs.patchValue(this.marketHubs[0].id);
        } else {
          this.errorMessage = 'No market hubs to display';
        }
      },
      err => this.errorMessage = err
    );
  }

  ngOnDestroy(): void {
    this.routeListener$.unsubscribe();
  }

  calculate() {
    const buyMarketHub: MarketHubModel = this.marketHubs.find((mh) => mh.id === +this.form.value.buyMarketHubs);
    const sellMarketHub: MarketHubModel = this.marketHubs.find((mh) => mh.id === +this.form.value.sellMarketHubs);
    const nrRuns: number = this.form.value.nrRuns;

    this.isCalculating = true;
    this.isCalculated = false;
    this.calculatorService.calculateBlueprint(this.bp, buyMarketHub, sellMarketHub, nrRuns).subscribe(
      (blueprintData: BlueprintModel) => {
        this.bp = blueprintData;
        this.isCalculating = false;
        this.isCalculated = true;
      });
  }

  calculateClass() {
    return {
      btn: true,
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
