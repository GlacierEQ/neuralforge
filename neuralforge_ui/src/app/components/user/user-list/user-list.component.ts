import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild,
  inject,
} from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatChipsModule } from "@angular/material/chips";
import { MatDialog } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatTableDataSource, MatTableModule } from "@angular/material/table";
import { IUser } from "../../../interfaces";
import { UserService } from "../../../services/user.service";
import { ConfirmDialogComponent } from "../../dialogs/confirm-dialog/confirm-dialog.component";
import { MatTooltipModule } from '@angular/material/tooltip';
import {MatOption, MatSelect} from "@angular/material/select";
import {AlertService} from "../../../services/alert.service";

@Component({
  selector: "app-user-list",
  standalone: true,
  imports: [
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    DatePipe,
    NgIf,
    MatTooltipModule,
    MatSelect,
    MatOption,
    NgForOf
  ],
  templateUrl: "./user-list.component.html",
  styleUrl: "./user-list.component.scss",
})
export class UserListComponent implements AfterViewInit {
  @Input() title: string = "";
  @Input() users: IUser[] = [];
  @Output() callBlockAction = new EventEmitter<IUser>();

  displayedColumns: string[] = [
    "id",
    "name",
    "lastName",
    "email",
    "createdAt",
    "role",
    "status",
    "verified",
    "actions",
  ];
  dataSource!: MatTableDataSource<IUser>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  private alertService = inject(AlertService);

  private dialog = inject(MatDialog);
  private userService = inject(UserService);

  roles: any[] = [];

  ngAfterViewInit() {
    this.dataSource = new MatTableDataSource(this.users);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.userService.getAllRoles().subscribe((res) => {
      this.roles = res;
    });
  }

  onRoleChange(user: IUser, newRoleId: string) {
    if (user.role?.id === newRoleId) return;

    const previousRoleId = user.role?.id;

    // Optimistically update the UI (optional - skip if you don't want the dropdown to change immediately)
    user.role!.id = newRoleId;

    this.userService.updateUserRole(user.id!, newRoleId).subscribe({
      next: () => {
        this.alertService.displayAlert(
            "info",
            "User updated successfully",
            "center",
            "top",
            ["error-snackbar"]
        );
      },
      error: (error: any) => {
        // Revert role change in UI
        if (previousRoleId != null) {
          user.role!.id! = previousRoleId;
        }

        this.alertService.displayAlert(
            "error",
            error?.error?.exception || "Failed to update user role",
            "center",
            "top",
            ["error-snackbar"]
        );
        console.error("Role update error:", error);
      },
    });
  }



  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  roleDisplayNameMap: { [key: string]: string } = {
    ROLE_ADMINISTRATOR: 'Administrator',
    ROLE_STUDENT: 'Student',
    ROLE_TEACHER: 'Teacher'
  };
  confirmToggleStatus(user: IUser) {
    const isBlocking = user.status;
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: isBlocking ? "Block User" : "Unblock User",
        message: `Are you sure you want to ${
          isBlocking ? "block" : "unblock"
        } this user?`,
        confirmText: isBlocking ? "Block" : "Unblock",
        cancelText: "Cancel",
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.userService.toggleStatus(user.id!).subscribe(() => {
          location.reload();
        });
      }
    });
  }
}
