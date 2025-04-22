import { CommonModule } from "@angular/common";
import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatTableModule } from "@angular/material/table";
import { Subscription } from "rxjs";
import { IDynamicContent } from "../../../interfaces";
import { ProjectMaterial } from "../../../models/project-material.model";
import { AlertService } from "../../../services/alert.service";
import { DynamicContentService } from "../../../services/dynamic-content.service";
import {
  MaterialUpdate,
  ProjectMaterialService,
} from "../../../services/project-material.service";
import { GenerateContentDialogComponent } from "../dialogs/generate-content-dialog/generate-content-dialog.component";

@Component({
  selector: "app-generated-contents",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatDialogModule,
  ],
  templateUrl: "./generated-contents.component.html",
  styleUrls: ["./generated-contents.component.scss"],
})
export class GeneratedContentsComponent implements OnInit, OnDestroy {
  @Input() projectId: string = "";
  @Input() projectMaterials: ProjectMaterial[] = [];
  contents: IDynamicContent[] = [];
  isLoading = false;
  displayedColumns: string[] = ["title", "type", "creationDate", "actions"];
  private subscriptions = new Subscription();

  constructor(
    private dialog: MatDialog,
    private dynamicContentService: DynamicContentService,
    private projectMaterialService: ProjectMaterialService,
    private alert: AlertService
  ) {}

  ngOnInit(): void {
    this.loadContents();
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

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  loadMaterials(): void {
    this.projectMaterialService
      .getMaterialsForProject(this.projectId)
      .subscribe({
        next: (materials) => {
          this.projectMaterials = materials;
        },
        error: (error) => {
          console.error("Error loading materials:", error);
          this.alert.displayAlert(
            "error",
            "Failed to load project materials",
            "center",
            "top"
          );
        },
      });
  }

  loadContents(): void {
    this.isLoading = true;
    this.dynamicContentService.getByProjectId(this.projectId).subscribe({
      next: (contents: IDynamicContent[]) => {
        this.contents = contents;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error("Error loading contents:", error);
        this.alert.displayAlert(
          "error",
          "Failed to load generated contents",
          "center",
          "top"
        );
        this.isLoading = false;
      },
    });
  }

  openGenerateDialog(): void {
    if (!this.projectMaterials || this.projectMaterials.length === 0) {
      this.alert.displayAlert(
        "warning",
        "No materials available for this project",
        "center",
        "top"
      );
      return;
    }

    const dialogRef = this.dialog.open(GenerateContentDialogComponent, {
      width: "500px",
      data: {
        projectId: this.projectId,
        materials: this.projectMaterials,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadContents();
      }
    });
  }

  downloadContent(content: IDynamicContent): void {
    if (!content.id) return;
  
    this.dynamicContentService.download(content.id).subscribe({
      next: (response: ArrayBuffer) => {
        let mimeType = "application/pdf";
        let extension = "pdf";
  
        switch (content.type) {
          case "PPT":
            mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            extension = "pptx";
            break;
          case "CONCEPTMAP":
            mimeType = "image/png";
            extension = "png";
            break;
          case "SUMMARY":
          default:
            mimeType = "application/pdf";
            extension = "pdf";
            break;
        }
  
        const blob = new Blob([response], { type: mimeType });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `${content.title}.${extension}`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      },
      error: (err: any) => {
        console.error("Error downloading content:", err);
        this.alert.displayAlert(
          "error",
          "Failed to download content",
          "center",
          "top"
        );
      },
    });
  }
}
