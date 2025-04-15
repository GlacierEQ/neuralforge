package com.cenfotec.p3.neuralforge_api.component;

import com.cenfotec.p3.neuralforge_api.model.entity.ProgrammedGoalProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.NotificationResource;
import com.cenfotec.p3.neuralforge_api.service.NotificationService;
import com.cenfotec.p3.neuralforge_api.service.ProgrammedGoalProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
public class CronComponent {

    @Autowired
    ProgrammedGoalProjectService programmedGoalProjectService;

    @Autowired
    NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void runDailyNotifications() {
        sendProgrammedGoalNotifications();
    }

    public void sendProgrammedGoalNotifications(){
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        List<ProgrammedGoalProjectEntity> projects = programmedGoalProjectService.findAllByNotifyTrue();

        for (ProgrammedGoalProjectEntity project : projects) {
            if (isDaySelected(project.getSelectedDays(), today)) {

                NotificationResource notif = NotificationResource.builder()
                        .description("Hey! It's " + today + " — time to work on your learning goal: " + project.getName())
                        .userId(project.getCreatorUserId())
                        .title("It's learning time! - Learning Project: "+project.getName())
                        .actionLabel("Learn Now!")
                        .redirectTo("/app/project/programmed_goal/"+project.getId())
                        .build();

                notificationService.createNotification(notif);
            }
        }
    }

    private boolean isDaySelected(SelectedDaysEntity selectedDays, DayOfWeek day) {
        return switch (day) {
            case MONDAY -> selectedDays.isMonday();
            case TUESDAY -> selectedDays.isTuesday();
            case WEDNESDAY -> selectedDays.isWednesday();
            case THURSDAY -> selectedDays.isThursday();
            case FRIDAY -> selectedDays.isFriday();
            case SATURDAY -> selectedDays.isSaturday();
            case SUNDAY -> selectedDays.isSunday();
        };
    }
}
