package world.runna.service;

import world.runna.model.Activity;
import world.runna.repository.ActivityRepository;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LiveTrackerService {

    @Autowired
    private ActivityRepository activityRepository;

    @Transactional // REQUIRED now with auto-commit=false
    public void saveActivity(Duration elapsed) {
        Activity activity = new Activity();
        activity.setDurationSeconds(elapsed.toSecondsPart());
        activityRepository.save(activity);
    }
}