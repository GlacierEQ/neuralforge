import {
    Component,
    Input,
    Output,
    EventEmitter
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { FormsModule } from '@angular/forms';
import {
    trigger,
    transition,
    style,
    animate
} from '@angular/animations';
import { Router } from '@angular/router';
import { INotification } from '../../../../interfaces';

@Component({
    selector: 'app-notification-overlay',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatDividerModule,
        MatButtonToggleModule
    ],
    templateUrl: './notification-overlay.component.html',
    styleUrls: ['./notification-overlay.component.scss'],
    animations: [
        trigger('fadeOut', [
            transition(':leave', [
                animate('200ms ease-in', style({ opacity: 0, transform: 'translateY(-10px)' }))
            ])
        ])
    ]
})
export class NotificationOverlayComponent {
    @Input() notifications: INotification[] = [];
    @Input() allNotifications: INotification[] = [];
    @Output() dismiss = new EventEmitter<string>();
    @Output() close = new EventEmitter<void>();

    viewMode: 'new' | 'all' = 'new';

    constructor(private router: Router) {}

    onBackdropClick(event: MouseEvent) {
        if ((event.target as HTMLElement).classList.contains('overlay-backdrop')) {
            this.close.emit();
        }
    }

    handleAction(notification: INotification) {
        if (notification.redirectTo) {
            this.router.navigate([notification.redirectTo]);
        }
        this.dismiss.emit(notification.id!);
        this.close.emit();
    }
}
