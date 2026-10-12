package world.runna.ui;
import world.runna.service.ActivityService;
import world.runna.service.LiveTrackerService;
import world.runna.ui.components.OsmMapComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;

import io.netty.util.concurrent.ScheduledFuture;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@Route("map")
@Push // <-- CRITICAL - add this to your View class
public class MapView extends VerticalLayout {

    @Autowired
    private LiveTrackerService liveTrackerService;  
    
    private Label timerLabel = new Label("00:00:00");
    private Button startBtn = new Button("Start");
    private Button stopBtn = new Button("Stop");
    private Button resetBtn = new Button("Reset");

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> timerHandle;
    private Instant startTime;
    private Duration elapsed = Duration.ZERO;
    private boolean running = false;

    public MapView() {
        timerLabel.getStyle().set("font-size", "2em").set("font-weight", "bold");
        
        startBtn.addClickListener(e -> start());
        stopBtn.addClickListener(e -> stop());
        resetBtn.addClickListener(e -> reset());

        stopBtn.setEnabled(false);

        add(timerLabel, new HorizontalLayout(startBtn, stopBtn, resetBtn));
        
        // Important for your Postgres fix
        // Don't load @Lob entities inside timer thread
    }

    private void start() {
        if (running) return;
        running = true;
        startTime = Instant.now().minus(elapsed);
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);

        UI ui = UI.getCurrent();
        executor = Executors.newSingleThreadScheduledExecutor();

        timerHandle = (ScheduledFuture<?>) executor.scheduleAtFixedRate(() -> {
            ui.access(() -> {
                elapsed = Duration.between(startTime, Instant.now());
                timerLabel.setText(format(elapsed));
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stop() {
        if (!running) return;
        running = false;
        if (timerHandle != null) timerHandle.cancel(false);
        if (executor != null) executor.shutdown();
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        
        // SAVE HERE - this is where you persist to Postgres
        // Must be @Transactional
        liveTrackerService.saveActivity(elapsed);
    }

    private void reset() {
        stop();
        elapsed = Duration.ZERO;
        timerLabel.setText("00:00:00");
    }

    private String format(Duration d) {
        long s = d.toSeconds();
        return String.format("%02d:%02d:%02d", s/3600, (s%3600)/60, s%60);
    }
}