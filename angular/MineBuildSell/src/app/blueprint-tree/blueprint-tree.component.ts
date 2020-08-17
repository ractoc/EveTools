import {Component, OnInit} from '@angular/core';
import {AssetsService} from '../service/assets.service';
import {Router} from '@angular/router';
import {ItemModel} from '../shared/model/item.model';
import {MarketGroupModel} from '../shared/model/marketgroup.model';
import {LocalStorageService} from '../service/local-storage.service';

@Component({
  selector: 'app-blueprint-tree',
  templateUrl: './blueprint-tree.component.html',
  styleUrls: ['./blueprint-tree.component.css']
})
export class BlueprintTreeComponent implements OnInit {

  constructor(private assetsService: AssetsService,
              private router: Router,
              private localStorageService: LocalStorageService) {
  }

  blueprintList: Array<ItemModel>;
  errorMessage: string;
  expanded: Array<MarketGroupModel> = new Array<MarketGroupModel>();

  ngOnInit() {
  }

  displayBlueprintList(marketGroup: MarketGroupModel) {
    if (marketGroup) {
      this.assetsService.getItemsForMarketGroup(marketGroup).subscribe(
        blueprints => {
          this.blueprintList = blueprints;
        },
        error => {
          if (error.status === 401) {
            this.localStorageService.set('currentPage', '/blueprints/all');
            this.router.navigateByUrl('/login');
          }
        }
      );
    } else {
      this.blueprintList = undefined;
    }
  }

}
