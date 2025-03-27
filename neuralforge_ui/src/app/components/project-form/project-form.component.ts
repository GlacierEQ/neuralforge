import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Input, Output } from "@angular/core";
import { FormsModule, NgForm } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";

export interface ProjectFormData {
  name: string;
  description: string;
  [key: string]: any;
}

@Component({
  selector: "app-project-form",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: "./project-form.component.html",
  styleUrls: ["./project-form.component.scss"],
})
export class ProjectFormComponent {
  @Input() projectData: ProjectFormData = {
    name: "",
    description: "",
  };

  @Input() submitButtonText: string = "Create";
  @Input() cancelButtonText: string = "Cancel";

  @Output() formSubmit = new EventEmitter<ProjectFormData>();
  @Output() formCancel = new EventEmitter<void>();

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.formSubmit.emit(this.projectData);
    }
  }

  onCancel() {
    this.formCancel.emit();
  }
}
