import {Component} from '@angular/core';
import {SearchResult} from "../../services/model/search-result";
import {MatDialogRef} from "@angular/material/dialog";
import {SearchService} from "../../services/search.service";
import {UniverseService} from "../../services/universe.service";

@Component({
  selector: 'app-search-solar-system-dialog',
  templateUrl: './search-universe-dialog.component.html',
  styleUrls: ['./search-universe-dialog.component.css']
})
export class SearchUniverseDialogComponent {
  search: string;

  searchResultList: SearchResult[] = [];

  searchResult: SearchResult;

  constructor(public dialogRef: MatDialogRef<SearchUniverseDialogComponent>,
              private searchService: SearchService,
              private universeService: UniverseService) {
  }

  searchEnabled() {
    return this.search && this.search.length > 3;
  }

  doSearch() {
    this.searchResultList = [];
    this.searchService.search(this.search, 'solar_system').subscribe(idList => {
      for (const id of idList) {
        this.loadSolarSystem(id);
      }
    })
  }

  private loadSolarSystem(id) {
    this.universeService.getSolarSystem(id).subscribe(solarSystem => {
      this.searchResultList.push(solarSystem);
    })
  }

  selectEntry(result: SearchResult) {
    console.log('selected: ', result);
    this.searchResult = result;
  }

  highlightRow(searchResult: SearchResult) {
    if (searchResult === this.searchResult) {
      return "highlight";
    } else {
      return "results";
    }
  }

  resetSearch() {
    this.searchResultList = [];
    this.search = undefined;
  }
}
