import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Role} from "../../services/model/role";
import {RoleService} from "../../services/role.service";

export interface RoleDialogData {
  fleetId: number;
}

@Component({
  selector: 'app-role-dialog',
  templateUrl: './role-dialog.component.html',
  styleUrls: ['./role-dialog.component.css']
})
export class RoleDialogComponent implements OnInit {

  roles: Role[];
  selectedRole: Role;

  constructor(public dialogRef: MatDialogRef<RoleDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: RoleDialogData,
              private roleService: RoleService) {
  }

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles() {
    this.roleService.loadRoles(this.data.fleetId).subscribe(
      (roleData: Role[]) => {
        console.log(roleData);
        this.roles = roleData;
      }
    );
  }

}
