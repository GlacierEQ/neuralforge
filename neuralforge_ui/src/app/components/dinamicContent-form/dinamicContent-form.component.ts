import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Input, Output } from "@angular/core";
import { FormsModule, NgForm } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";

export interface DynamicContentFormData {
  file: File | null;
  title: string;
  email: string;
  type: string;
  projectId: string;
}

@Component({
  selector: "app-dynamic-content-form",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: "./dynamicContent-form.component.html",
  styleUrls: ["./dynamicContent-form.component.scss"],
})
export class DynamicContentFormComponent {
  @Input() formData: DynamicContentFormData = {
    file: null,
    title: "",
    email: "",
    type: "",
    projectId: "",
  };

  @Input() submitButtonText: string = "Generate";
  @Input() cancelButtonText: string = "Cancel";
  @Input() disableSubmit: boolean = false;

  @Output() formSubmit = new EventEmitter<DynamicContentFormData>();
  @Output() formCancel = new EventEmitter<void>();

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.formData.file = input.files[0];
    }
  }

  onSubmit(form: NgForm) {
    if (form.valid && this.formData.file) {
      this.formSubmit.emit(this.formData);
    }
  }

  onCancel() {
    this.formCancel.emit();
  }
}