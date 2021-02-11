import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Invitation} from "../../services/model/invitation";
import {SearchService} from "../../services/search.service";
import {CharacterService} from "../../services/character.service";
import {CorporationService} from "../../services/corporation.service";
import {SearchResult} from "../../services/model/search-result";
import {UserService} from "../../services/user.service";

export interface InvitationDialogData {
  fleetId: number;
}

@Component({
  selector: 'app-invitation-dialog',
  templateUrl: './invitation-dialog.component.html',
  styleUrls: ['./invitation-dialog.component.css']
})
export class InvitationDialogComponent implements OnInit {
  searchType: string;
  invitation: Invitation;
  search: string;

  searchResultList: SearchResult[] = [];

  TYPE_CHARACTER = 'character';
  TYPE_CORPORATION = 'corporation';
  private searchResult: SearchResult;

  constructor(public dialogRef: MatDialogRef<InvitationDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: InvitationDialogData,
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
