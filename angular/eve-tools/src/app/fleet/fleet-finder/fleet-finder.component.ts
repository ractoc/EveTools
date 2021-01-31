import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {TypeService} from "../../services/type.service";
import {Type} from "../../services/model/type";
import {UserService} from "../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LocalStorageService} from "../../../../../MineBuildSell/src/app/service/local-storage.service";

@Component({
  selector: 'app-fleet-finder',
  templateUrl: './fleet-finder.component.html',
  styleUrls: ['./fleet-finder.component.css']
})
export class FleetFinderComponent implements OnInit {

  fleetForm = new FormGroup({
    start: new FormControl(),
    end: new FormControl(),
    types: new FormControl(),
  });

  private types: Type[];

  constructor(private router: Router,
              private userService: UserService,
              private typeService: TypeService,
              private localStorageService: LocalStorageService) {
  }

  ngOnInit(): void {
    if (!this.userService.getCurrentUser()) {
      this.localStorageService.set('currentPage', '/fleet/find');
      this.router.navigateByUrl('/user/login');
    }
    this.loadTypes();
  }

  private loadTypes() {
    this.typeService.loadTypes().subscribe(
      (typeData: Type[]) => {
        console.log('setting types', typeData);
        this.types = typeData;
      }
    );
  }

}
