import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SearchResultModel} from '../shared/model/searchResult.model';
import {SearchService} from "../service/search.service";
import {CharacterService} from "../service/character.service";

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  @Input() searchCharacter: boolean;
  @Input() searchCorporation: boolean;

  @Output() searchResult = new EventEmitter<SearchResultModel>();

  TYPE_CHARACTER = 'character';
  TYPE_CORPORATION = 'corporation';

  searchType = this.TYPE_CHARACTER;
  search: string;
  searchResultList: SearchResultModel[] = [];

  constructor(private searchService: SearchService,
              private characterService: CharacterService) {
  }

  ngOnInit(): void {
  }

  searchEnabled() {
    return this.search && this.search.length > 5;
  }

  doSearch() {
    if (this.searchType === this.TYPE_CHARACTER) {
      this.searchService.searchCharacters(this.search).subscribe(characterIds => {
        for (const characterId of characterIds) {
          console.log('characterId', characterId);
          this.characterService.getCharacter(characterId).subscribe(character => {
            this.characterService.getPortrait(characterId).subscribe(portrait => {
              character.portrait = portrait;
            })
            this.searchResultList.push(character);
          })
        }
      })
    }
  }

  selectEntry(result: SearchResultModel) {
    this.searchResult.emit(result);
  }
}
