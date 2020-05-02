import {Component, OnInit} from '@angular/core';
import {AssetsService} from '../service/assets.service';
import {MarketGroupModel} from '../shared/model/marketgroup.model';
import {Router} from '@angular/router';
import {ItemModel} from '../shared/model/item.model';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css']
})
export class ItemListComponent implements OnInit {

  constructor(private assetsService: AssetsService, private router: Router) {
  }

  private displayItemsForGroup: MarketGroupModel;

  itemList: Array<ItemModel>;
  marketGroups: Array<MarketGroupModel> = new Array<MarketGroupModel>();
  errorMessage: string;
  expanded: Array<MarketGroupModel> = new Array<MarketGroupModel>();

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
    this.expanded = this.determineExpanded(marketGroup);
    if (!marketGroup.children) {
      this.assetsService.getMarketGroups(marketGroup.id).subscribe(
        groups => {
          marketGroup.children = groups;
          console.log('looking up market group', marketGroup.name);
          console.log('received children listing', marketGroup.children);
          if (marketGroup.children.length === 0) {
            this.displayItemList(marketGroup);
          } else {
            this.displayItemsForGroup = undefined;
            this.itemList = undefined;
          }
        },
        error => {
          if (error.status === 401) {
            localStorage.setItem('currentPage', '/items');
            this.router.navigateByUrl('/login');
          }
        }
      );
    } else {
      if (marketGroup.children.length === 0) {
        this.displayItemList(marketGroup);
      } else {
        this.displayItemsForGroup = undefined;
      }
    }
  }

  collapseId(item: any) {
    return 'collapse_' + item.id;
  }

  determineExpanded(marketGroup: MarketGroupModel) {
    const crumbtrail = new Array<MarketGroupModel>();
    crumbtrail.push(marketGroup);
    if (marketGroup.parentGroupId !== 0) {
      const parent = this.expanded.find(item => item.id === marketGroup.parentGroupId);
      this.determineExpanded(parent).forEach(item => crumbtrail.push(item));
    }
    return crumbtrail;
  }

  private displayItemList(marketGroup: MarketGroupModel) {
    this.assetsService.getItemsForMarketGroup(marketGroup).subscribe(
      items => {
        console.log('received items', items);
        this.itemList = items;
      },
      error => {
        if (error.status === 401) {
          localStorage.setItem('currentPage', '/items');
          this.router.navigateByUrl('/login');
        }
      }
    );
  }
}
