import { Component, inject, ViewChild } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { LoaderComponent } from "../../components/loader/loader.component";
import { UserListComponent } from "../../components/user/user-list/user-list.component";
import { IUser } from "../../interfaces";
import { ModalService } from "../../services/modal.service";
import { UserService } from "../../services/user.service";

@Component({
  selector: "app-users",
  standalone: true,
  imports: [UserListComponent, LoaderComponent],
  templateUrl: "./users.component.html",
  styleUrl: "./users.component.scss",
})
export class UsersComponent {
  public modalService: ModalService = inject(ModalService);
  @ViewChild("addUsersModal") public addUsersModal: any;
  public fb: FormBuilder = inject(FormBuilder);
  userForm = this.fb.group({
    id: [""],
    email: ["", Validators.required, Validators.email],
    name: ["", Validators.required],
    lastName: ["", Validators.required],
    password: ["", Validators.required],
  });

  users$: IUser[];

  constructor(public userService: UserService) {
    this.userService.search.page = 1;
    this.userService.getAll();
    this.users$ = this.userService.users$();
  }

  saveUser(user: IUser) {
    this.userService.save(user);
    this.modalService.closeAll();
  }

  callEdition(user: IUser) {
    this.userForm.controls["id"].setValue(
      user.id ? JSON.stringify(user.id) : ""
    );
    this.userForm.controls["email"].setValue(user.email ? user.email : "");
    this.userForm.controls["name"].setValue(
      user.name ? JSON.stringify(user.name) : ""
    );
    this.userForm.controls["lastName"].setValue(
      user.lastName ? JSON.stringify(user.lastName) : ""
    );
    this.userForm.controls["password"].setValue(
      user.password ? JSON.stringify(user.password) : ""
    );
    this.modalService.displayModal("md", this.addUsersModal);
  }

  updateUser(user: IUser) {
    this.userService.update(user);
    this.modalService.closeAll();
  }
}
