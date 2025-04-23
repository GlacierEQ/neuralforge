import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatIcon } from "@angular/material/icon";
import { RouterLink } from "@angular/router";

import { EmptyStateComponent } from "../../../components/empty-state/empty-state.component";

import {
  IProjectType,
  ILearningProject,
  ITeachingProject,
  IProgrammedGoalProject,
} from "../../../interfaces";

import { ProjectService } from "../../../services/project.service";
import { MatFormField, MatLabel } from "@angular/material/form-field";
import { FormsModule } from "@angular/forms";
import { MatInput } from "@angular/material/input";
import {MatPaginator, PageEvent} from "@angular/material/paginator";

@Component({
  selector: "app-project-global-view",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIcon,
    EmptyStateComponent,
    RouterLink,
    MatFormField,
    FormsModule,
    MatInput,
    MatLabel,
    MatPaginator,
  ],
  templateUrl: "./project-global-view.component.html",
  styleUrls: ["./project-global-view.component.scss"],
})
export class ProjectGlobalViewComponent implements OnInit {
  selectedProjectType: IProjectType = IProjectType.Teaching;
  searchTerm: string = "";

  currentPage: number = 0;
  pageSize: number = 5;

  allProjects: {
    [key in IProjectType]: {
      isLoading: boolean;
      hasError: boolean;
      errorMessage: string;
      cards: {
        id: string;
        title: string;
        content: string;
        projectType: IProjectType;
      }[];
    };
  } = {
    [IProjectType.Teaching]: {
      isLoading: false,
      hasError: false,
      errorMessage: "",
      cards: [],
    },
    [IProjectType.Learning]: {
      isLoading: false,
      hasError: false,
      errorMessage: "",
      cards: [],
    },
    [IProjectType.ProgrammedGoal]: {
      isLoading: false,
      hasError: false,
      errorMessage: "",
      cards: [],
    },
  };

  constructor(private projectService: ProjectService) {}

  ngOnInit() {
    this.fetchAllProjects();
  }

  fetchAllProjects() {
    Object.keys(this.allProjects).forEach((key) => {
      const type = key as IProjectType;
      this.allProjects[type].isLoading = true;
      this.allProjects[type].hasError = false;
    });

    this.projectService.getAllProjects().subscribe({
      next: (response) => {
        if (response.teachingProjects) {
          this.allProjects[IProjectType.Teaching].cards = response.teachingProjects.map(
              (project: ITeachingProject) => ({
                id: project.id,
                title: project.name,
                content: project.description || "No description available.",
                projectType: IProjectType.Teaching,
              })
          );
        }

        if (response.learningProjects) {
          this.allProjects[IProjectType.Learning].cards = response.learningProjects.map(
              (project: ILearningProject) => ({
                id: project.id,
                title: project.name,
                content: project.description || "No description available.",
                projectType: IProjectType.Learning,
              })
          );
        }

        if (response.programmedGoalProjects) {
          this.allProjects[IProjectType.ProgrammedGoal].cards = response.programmedGoalProjects.map(
              (project: IProgrammedGoalProject) => ({
                id: project.id,
                title: project.name,
                content: project.description || "No description available.",
                projectType: IProjectType.ProgrammedGoal,
              })
          );
        }

        Object.keys(this.allProjects).forEach((key) => {
          const type = key as IProjectType;
          this.allProjects[type].isLoading = false;
        });
      },
      error: () => {
        Object.keys(this.allProjects).forEach((key) => {
          const type = key as IProjectType;
          this.allProjects[type].isLoading = false;
          this.allProjects[type].hasError = true;
          this.allProjects[type].errorMessage =
              "Unable to load projects. Please try again later.";
        });
      },
    });
  }

  get filteredProjects() {
    return this.allProjects[this.selectedProjectType].cards.filter((project) =>
        (project.title?.toLowerCase() || "").includes(this.searchTerm.toLowerCase()) ||
        (project.content?.toLowerCase() || "").includes(this.searchTerm.toLowerCase())
    );
  }

  get paginatedProjects() {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredProjects.slice(start, end);
  }

  handlePageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  onTabChange() {
    this.currentPage = 0;
  }

  getRouteByProjectType(projectType: IProjectType): string {
    switch (projectType) {
      case IProjectType.Learning:
        return "project/learning";
      case IProjectType.Teaching:
        return "project/teaching";
      case IProjectType.ProgrammedGoal:
        return "project/programmed_goal";
      default:
        return "";
    }
  }

  protected readonly IProjectType = IProjectType;
}
