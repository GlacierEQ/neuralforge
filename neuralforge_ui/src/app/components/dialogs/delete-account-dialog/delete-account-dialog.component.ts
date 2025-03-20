import { Component, OnInit } from "@angular/core";
import { FormControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-delete-account-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
  ],
  templateUrl: "./delete-account-dialog.component.html",
  styleUrls: ["./delete-account-dialog.component.scss"],
})
export class DeleteAccountDialogComponent implements OnInit {
  confirmationControl = new FormControl("", [
    Validators.required,
    Validators.pattern("Eliminar"),
  ]);

  constructor(public dialogRef: MatDialogRef<DeleteAccountDialogComponent>) {}

  ngOnInit(): void {}
}
