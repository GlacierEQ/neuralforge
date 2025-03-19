import { Component, OnInit, inject } from "@angular/core";
import { Router, RouterLink } from "@angular/router";
import { IUser } from "../../interfaces";
import { AuthService } from "../../services/auth.service";
import { ProfileService } from "../../services/profile.service";

@Component({
  selector: "app-my-account",
  standalone: true,
  imports: [RouterLink],
  templateUrl: "./my-account.component.html",
})
export class MyAccountComponent implements OnInit {
  private authService = inject(AuthService);
  private profileService = inject(ProfileService);

  userName = this.profileService.userName;

  constructor(public router: Router) {}

  ngOnInit() {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    this.authService.getCurrentUser().subscribe({
      next: (user: IUser) => {
        this.profileService.refreshFromApi(user);
      },
      error: (error) => {
        console.error("Error fetching current user:", error);
      },
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl("/login");
  }
}
