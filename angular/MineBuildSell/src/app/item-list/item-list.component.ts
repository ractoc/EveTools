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

  itemList: Array<ItemModel>;
  errorMessage: string;
  expanded: Array<MarketGroupModel> = new Array<MarketGroupModel>();

  ngOnInit() {
  }

  displayItemList(marketGroup: MarketGroupModel) {
    if (marketGroup) {
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
    } else {
      this.itemList = undefined;
    }
  }
}
