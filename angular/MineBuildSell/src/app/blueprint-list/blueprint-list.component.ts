import {Component, OnInit} from '@angular/core';
import {BlueprintModel} from '../shared/model/blueprint.model';
import {CharacterService} from '../service/character.service';

@Component({
  selector: 'app-blueprint-list',
  templateUrl: './blueprint-list.component.html',
  styleUrls: ['./blueprint-list.component.css']
})
export class BlueprintListComponent implements OnInit {

  blueprints: BlueprintModel[];
  errorMessage: string;

  constructor(private characterService: CharacterService) {
  }

  ngOnInit() {
    this.errorMessage = undefined;
    this.characterService.getBlueprints().subscribe(
      (blueprintData: BlueprintModel[]) =>
        blueprintData && blueprintData.length > 0
          ? this.blueprints = blueprintData
          : this.errorMessage = 'No blueprints to display',
      err => this.errorMessage = err
    );
  }

  blueprintClass(blueprint: BlueprintModel) {
    return {
      'list-group-item': true,
      'list-group-item-action': true,
    };
  }

}
