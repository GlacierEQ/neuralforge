import { inject } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CanDeactivateFn } from "@angular/router";
import { firstValueFrom } from "rxjs";
import { ConfirmDialogComponent } from "../components/dialogs/confirm-dialog/confirm-dialog.component";
import { ProfileComponent } from "../pages/profile/profile.component";

export const editProfileDeactivateGuard: CanDeactivateFn<
  ProfileComponent
> = async (component) => {
  if (component.isEditing && component.isDirty) {
    const dialog = inject(MatDialog);

    const result = await firstValueFrom(
      dialog
        .open(ConfirmDialogComponent, {
          data: {
            title: "Cambios sin guardar",
            message: "Tienes cambios sin guardar. ¿Deseas salir?",
            confirmText: "Salir",
            cancelText: "Cancelar",
          },
        })
        .afterClosed()
    );

    return result ?? false;
  }
  return true;
};
