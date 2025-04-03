import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild,
  inject,
} from '@angular/core';
import { IUser } from '../../../interfaces';
import {DatePipe, NgIf} from '@angular/common';
import {
  MatTableDataSource,
  MatTableModule
} from "@angular/material/table";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatChipsModule } from "@angular/material/chips";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../../services/user.service';
import {ConfirmDialogComponent} from "../../dialogs/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'app-user-list',
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
    NgIf
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss',
})
export class UserListComponent implements AfterViewInit {
  @Input() title: string = '';
  @Input() users: IUser[] = [];
  @Output() callBlockAction = new EventEmitter<IUser>();

  displayedColumns: string[] = ['id', 'name', 'lastname', 'email', 'createdAt', 'role', 'status', 'verified', 'actions'];
  dataSource!: MatTableDataSource<IUser>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  private dialog = inject(MatDialog);
  private userService = inject(UserService);

  ngAfterViewInit() {
    this.dataSource = new MatTableDataSource(this.users);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  confirmToggleStatus(user: IUser) {
    const isBlocking = user.status;
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: isBlocking ? 'Block User' : 'Unblock User',
        message: `Are you sure you want to ${isBlocking ? 'block' : 'unblock'} this user?`,
        confirmText: isBlocking ? 'Block' : 'Unblock',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userService.toggleStatus(user.id!).subscribe(() => {
          location.reload();
        });
      }
    });
  }
}
