import {Component, OnInit} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {SearchService} from "../../services/search.service";
import {CharacterService} from "../../services/character.service";
import {CorporationService} from "../../services/corporation.service";
import {SearchResult} from "../../services/model/search-result";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'search-char-corp-dialog',
  templateUrl: './search-char-corp-dialog.component.html',
  styleUrls: ['./search-char-corp-dialog.component.css']
})
export class SearchCharCorpDialogComponent implements OnInit {
  search: string;

  searchResultList: SearchResult[] = [];

  searchResult: SearchResult;

  constructor(public dialogRef: MatDialogRef<SearchCharCorpDialogComponent>,
              private searchService: SearchService,
              private characterService: CharacterService,
              private corporationService: CorporationService,
              private userService: UserService) { }

  ngOnInit(): void {
  }

  searchEnabled() {
    return this.search && this.search.length > 3;
  }

  doSearch() {
    this.searchResultList = [];
    this.searchService.search(this.search, 'character').subscribe(idList => {
      for (const id of idList) {
        this.loadCharacter(id);
      }
    })
  }

  private loadCharacter(id) {
    this.characterService.getCharacter(id).subscribe(character => {
      this.characterService.getPortrait(id).subscribe(portrait => {
        character.portrait = portrait;
      })
      this.searchResultList.push(character);
    })
  }

  private loadCorporation(id) {
    this.corporationService.getCorporation(id).subscribe(corporation => {
      this.corporationService.getIcon(id).subscribe(portrait => {
        corporation.portrait = portrait;
      })
      this.searchResultList.push(corporation);
      this.selectEntry(corporation);
    })
  }

  selectEntry(result: SearchResult) {
    this.searchResult = result;
  }

  selectMyCorporation() {
    this.resetSearch();
    this.characterService.getCharacter(this.userService.getCurrentUser().characterId).subscribe(character => {
      this.loadCorporation(character.corporationId);
    })
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
