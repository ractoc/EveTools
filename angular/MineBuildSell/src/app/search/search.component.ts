import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SearchResultModel} from '../shared/model/searchResult.model';
import {SearchService} from "../service/search.service";
import {CharacterService} from "../service/character.service";
import {CorporationService} from "../service/corporation.service";
import {UserService} from "../service/user.service";

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
              private characterService: CharacterService,
              private corporationService: CorporationService,
              private userService: UserService) {
  }

  ngOnInit(): void {
  }

  searchEnabled() {
    return this.search && this.search.length > 5;
  }

  doSearch() {
    this.searchService.search(this.search, this.searchType).subscribe(idList => {
      for (const id of idList) {
        if (this.searchType === this.TYPE_CHARACTER) {
          this.loadCharacter(id);
        } else if (this.searchType === this.TYPE_CORPORATION) {
          this.loadCorporation(id);
        }
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
    })
  }

  selectEntry(result: SearchResultModel) {
    this.searchResult.emit(result);
  }

  selectMyCorporation() {
    this.characterService.getCharacter(this.userService.getCurrentUser().characterId).subscribe(character => {
      this.corporationService.getCorporation(character.corporationId).subscribe(corporation => {
        this.searchResult.emit(corporation);
      })
    })
  }
}
