import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSliderModule } from '@angular/material/slider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { QuizService } from '../../../services/quiz.service';
import { AlertService } from '../../../services/alert.service';

@Component({
  selector: 'app-quiz-generate-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSliderModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './quiz-generate-dialog.component.html',
  styleUrls: ['./quiz-generate-dialog.component.scss']
})
export class QuizGenerateDialogComponent {
  generateForm: FormGroup;
  isLoading = false;

  constructor(
    private dialogRef: MatDialogRef<QuizGenerateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { projectId: string },
    private formBuilder: FormBuilder,
    private quizService: QuizService,
    private alertService: AlertService
  ) {
    this.generateForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', Validators.maxLength(1000)],
      questionCount: [5, [Validators.required, Validators.min(1), Validators.max(15)]]
    });
  }

  onSubmit(): void {
    if (this.generateForm.invalid) {
      return;
    }

    this.isLoading = true;
    const { title, description, questionCount } = this.generateForm.value;

    this.quizService.generateQuiz(
      this.data.projectId,
      title,
      description || '',
      questionCount
    ).subscribe({
      next: (quiz) => {
        this.isLoading = false;
        this.alertService.displayAlert('success', 'Quiz generated successfully');
        this.dialogRef.close(quiz);
      },
      error: (err) => {
        this.isLoading = false;
        this.alertService.displayAlert('error', 'Failed to generate quiz. Please try again.');
        console.error('Error generating quiz:', err);
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
