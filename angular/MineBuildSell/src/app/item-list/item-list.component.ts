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

  constructor(private assetsService: AssetsService, private router: Router) {
  }

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
      'list-group-item-action': true,
    };
  }

  isExpanded(mgItem: any) {
    return this.expanded.find(e => e === mgItem) !== undefined;
  }

  selectMarketGroup(mgItem: any) {
    if (mgItem.children.length > 0) {
      if (this.isExpanded(mgItem)) {
        this.expanded = this.expanded.filter(item => item !== mgItem);
      } else {
        this.expanded.push(mgItem);
      }
    } else {
      console.log('Show item list');
    }
  }

  collapseId(item: any) {
    return 'collapse_' + item.id;
  }
}
