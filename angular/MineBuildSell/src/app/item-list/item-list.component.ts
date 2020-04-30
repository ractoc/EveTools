import {Component, OnInit} from '@angular/core';
import {AssetsService} from '../service/assets.service';
import {MarketGroupModel} from '../shared/model/marketgroup.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css']
})
export class ItemListComponent implements OnInit {
  private displayItemsForGroup: MarketGroupModel;

  constructor(private assetsService: AssetsService, private router: Router) {
  }

  marketGroups: Array<MarketGroupModel> = new Array<MarketGroupModel>();
  errorMessage: string;

  expanded: Array<MarketGroupModel> = new Array<MarketGroupModel>();
  selectedMarketGroup: MarketGroupModel;

  ngOnInit() {
    this.assetsService.getMarketGroups(0).subscribe(
      groups => {
        this.marketGroups = groups;
      },
      error => {
        if (error.status === 401) {
          localStorage.setItem('currentPage', '/items');
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
    this.selectedMarketGroup = marketGroup;
    this.expanded = this.determineExpanded(marketGroup);
    if (marketGroup.children.length === 0) {
      this.displayItemsForGroup = this.selectedMarketGroup;
    } else {
      this.displayItemsForGroup = undefined;
    }
  }

  collapseId(item: any) {
    return 'collapse_' + item.id;
  }

  private determineExpanded(marketGroup: MarketGroupModel) {
    const crumbtrail = new Array<MarketGroupModel>();
    crumbtrail.push(marketGroup);
    if (marketGroup.parentGroupId !== 0) {
      const parent = this.expanded.find(item => item.id === marketGroup.parentGroupId);
      this.determineExpanded(parent).forEach(item => crumbtrail.push(item));
    }
    return crumbtrail;
  }
}
