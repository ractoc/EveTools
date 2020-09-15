import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Subscription} from 'rxjs';

import {MarketHubModel} from '../shared/model/markethub.model';
import {UniverseService} from '../service/universe.service';
import {CalculatorService} from '../service/calculator.service';
import {AssetsService} from '../service/assets.service';
import {ItemModel} from '../shared/model/item.model';
import {LocalStorageService} from '../service/local-storage.service';

@Component({
  selector: 'app-item-details',
  templateUrl: './item-details.component.html',
  styleUrls: ['./item-details.component.css']
})
export class ItemDetailsComponent implements OnInit, OnDestroy {
  private routeListener$: Subscription;

  public errorMessage: string;
  public isCalculated: boolean;
  public item: ItemModel;
  public form: FormGroup;
  public marketHubs: MarketHubModel[];
  public isCalculating: boolean;

  constructor(private assetsService: AssetsService,
              private universeService: UniverseService,
              private calculatorService: CalculatorService,
              private route: ActivatedRoute,
              private router: Router,
              private formBuilder: FormBuilder,
              private localStorageService: LocalStorageService) {
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
            this.assetsService.getItem(params.id).subscribe(
              (itemData: ItemModel) => {
                this.item = itemData;
              },
              err => {
                if (err.status === 401) {
                  this.localStorageService.set('currentPage', '/item/' + params.id);
                  this.router.navigateByUrl('/login');
                } else {
                  this.errorMessage = 'Unable to load blueprint';
                }
              }
            );
          } else {
            this.item = undefined;
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
    this.calculatorService.calculateItem(this.item, buyMarketHub, sellMarketHub, nrRuns).then(
      (itemData: ItemModel) => {
        this.item = itemData;
        this.isCalculating = false;
        this.isCalculated = true;
      }).catch(error => {
      this.errorMessage = error.message;
      this.isCalculating = false;
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
