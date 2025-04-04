import { CommonModule } from "@angular/common";
import {
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Subscription } from "rxjs";
import { ConfirmDialogComponent } from "../../../components/dialogs/confirm-dialog/confirm-dialog.component";
import { ProjectMaterial } from "../../../models/project-material.model";
import {
  MaterialUpdate,
  ProjectMaterialService,
} from "../../../services/project-material.service";
import { UploadMaterialDialogComponent } from "./upload-material-dialog/upload-material-dialog.component";

@Component({
  selector: "app-materials",
  templateUrl: "./materials.component.html",
  styleUrls: ["./materials.component.scss"],
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
})
export class MaterialsComponent implements OnInit, OnChanges, OnDestroy {
  @Input() projectId: string = "";
  @Input() projectMaterials: ProjectMaterial[] = [];
  isLoading = false;
  private subscriptions = new Subscription();

  constructor(
    private projectMaterialService: ProjectMaterialService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    if (!this.projectMaterials || this.projectMaterials.length === 0) {
      this.loadMaterials();
    }

    this.subscriptions.add(
      this.projectMaterialService.materialUpdates$.subscribe(
        (update: MaterialUpdate | null) => {
          if (update && update.projectId === this.projectId) {
            this.subscriptions.add(
              this.projectMaterialService
                .getMaterialsForProject(this.projectId)
                .subscribe((materials) => {
                  this.projectMaterials = materials;
                })
            );
          }
        }
      )
    );
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["projectId"] && !changes["projectId"].firstChange) {
      this.loadMaterials();
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  loadMaterials(): void {
    this.isLoading = true;
    this.projectMaterialService
      .getMaterialsForProject(this.projectId)
      .subscribe({
        next: (materials) => {
          this.projectMaterials = materials;
          this.isLoading = false;
        },
        error: (error) => {
          console.error("Error loading materials:", error);
          this.snackBar.open("Failed to load project materials", "Close", {
            duration: 3000,
          });
          this.isLoading = false;
        },
      });
  }

  openUploadDialog(): void {
    this.dialog.open(UploadMaterialDialogComponent, {
      width: "500px",
      data: { projectId: this.projectId },
    });
  }

  deleteMaterial(material: ProjectMaterial): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "400px",
      data: {
        title: "Delete Material",
        message:
          material.type === "hyperlink"
            ? `Are you sure you want to delete this link: ${material.hyperlink}?`
            : `Are you sure you want to delete ${
                material.fileName || "this material"
              }?`,
        confirmText: "Delete",
        cancelText: "Cancel",
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.isLoading = true;
        this.projectMaterialService.deleteMaterial(material.id).subscribe({
          next: () => {
            this.projectMaterials = this.projectMaterials.filter(
              (item) => item.id !== material.id
            );

            this.snackBar.open("Material deleted successfully", "Close", {
              duration: 3000,
            });
            this.isLoading = false;
          },
          error: (error) => {
            console.error("Error deleting material:", error);
            this.snackBar.open("Failed to delete material", "Close", {
              duration: 3000,
            });
            this.isLoading = false;
          },
        });
      }
    });
  }

  downloadMaterial(material: ProjectMaterial): void {
    this.isLoading = true;
    this.projectMaterialService.downloadMaterialFile(material.id).subscribe({
      next: (blob) => {
        this.isLoading = false;

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = material.fileName || `download-${material.id}`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        this.snackBar.open(
          `${material.fileName} downloaded successfully`,
          "Close",
          {
            duration: 3000,
            panelClass: "success-snackbar",
          }
        );
      },
      error: (error) => {
        this.isLoading = false;

        let errorMessage = "Error downloading file";
        if (error.status === 403) {
          errorMessage = "You don't have permission to download this file";
        } else if (error.status === 404) {
          errorMessage = "File not found. It may have been deleted.";
        }

        this.snackBar.open(errorMessage, "Close", {
          duration: 5000,
          panelClass: "error-snackbar",
        });
      },
    });
  }

  getMaterialIcon(type: string): string {
    return type === "file" ? "insert_drive_file" : "link";
  }
}
