import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Router} from '@angular/router';

import {MarketGroupModel} from '../shared/model/marketgroup.model';
import {AssetsService} from '../service/assets.service';
import {LocalStorageService} from '../service/local-storage.service';

@Component({
  selector: 'app-market-group-tree',
  templateUrl: './market-group-tree.component.html',
  styleUrls: ['./market-group-tree.component.css']
})
export class MarketGroupTreeComponent implements OnInit {

  @Input() rootMarketGroupId: number;
  @Output() displayMarketGroupItems = new EventEmitter<MarketGroupModel>();

  marketGroups: Array<MarketGroupModel> = new Array<MarketGroupModel>();
  errorMessage: string;
  expanded: Array<MarketGroupModel> = new Array<MarketGroupModel>();

  constructor(private assetsService: AssetsService,
              private router: Router,
              private localStorageService: LocalStorageService) {
  }

  ngOnInit() {
    this.assetsService.getMarketGroups(this.rootMarketGroupId).subscribe(
      groups => {
        this.marketGroups = groups;
      },
      error => {
        if (error.status === 401) {
          this.localStorageService.set('currentPage', '/items');
          this.router.navigateByUrl('/login');
        }
      }
    );
  }

  marketGroupClass(marketGroup: MarketGroupModel) {
    return {
      'list-group-item': true,
      'border-0': true,
      active: this.expanded.find(mg => mg === marketGroup)
    };
  }

  isExpanded(mgItem: any) {
    return this.expanded.find(e => e === mgItem) !== undefined;
  }

  selectMarketGroup(marketGroup: MarketGroupModel) {
    this.expanded = this.determineExpanded(marketGroup);
    if (!marketGroup.children) {
      this.assetsService.getMarketGroups(marketGroup.id).subscribe(
        groups => {
          marketGroup.children = groups;
          if (marketGroup.children.length === 0) {
            this.displayMarketGroupItems.emit(marketGroup);
          } else {
            this.displayMarketGroupItems.emit(undefined);
          }
        },
        error => {
          if (error.status === 401) {
            this.localStorageService.set('currentPage', '/items');
            this.router.navigateByUrl('/login');
          }
        }
      );
    } else {
      if (marketGroup.children.length === 0) {
        this.displayMarketGroupItems.emit(marketGroup);
      } else {
        this.displayMarketGroupItems.emit(undefined);
      }
    }
  }

  collapseId(item: any) {
    return 'collapse_' + item.id;
  }

  determineExpanded(marketGroup: MarketGroupModel) {
    const crumbtrail = new Array<MarketGroupModel>();
    crumbtrail.push(marketGroup);
    if (marketGroup.parentGroupId !== +this.rootMarketGroupId) {
      const parent = this.expanded.find(item => item.id === marketGroup.parentGroupId);
      this.determineExpanded(parent).forEach(item => crumbtrail.push(item));
    }
    return crumbtrail;
  }

}
