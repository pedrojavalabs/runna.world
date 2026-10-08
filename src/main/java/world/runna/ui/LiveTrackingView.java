
package world.runna.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.Route;
import world.runna.ui.components.OsmMapComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Route(value="track", layout=MainLayout.class)
public class LiveTrackingView extends VerticalLayout {

    private OsmMapComponent map;
    private H2 timerLabel = new H2("00:00:00");
    private Span distanceLabel = new Span("0.00 km");
    private Span paceLabel = new Span("-- /km");
    private Span elevationLabel = new Span("0 m ↑");
    private Div statusDot = new Div();

    private Button startBtn, pauseBtn, stopBtn;
    private boolean tracking = false;
    private boolean paused = false;
    private List<double[]> track = new ArrayList<>();

    public LiveTrackingView(){
        setSizeFull(); setPadding(true); setSpacing(true);
        addClassName("live-tracking-view");

        // Header stats
        timerLabel.getStyle().set("margin","0").set("font-size","3rem").set("font-weight","800").set("font-variant-numeric","tabular-nums");
        distanceLabel.getStyle().set("font-size","1.4rem").set("font-weight","600");
        paceLabel.getStyle().set("font-size","1.2rem").set("color","var(--lumo-secondary-text-color)");
        elevationLabel.getStyle().set("font-size","1.2rem").set("color","var(--lumo-secondary-text-color)");

        statusDot.getStyle().set("width","12px").set("height","12px").set("border-radius","50%").set("background","#ccc").set("display","inline-block");

        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull(); stats.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        VerticalLayout left = new VerticalLayout(timerLabel, new HorizontalLayout(statusDot, new Span("GPS ready"))); left.setPadding(false); left.setSpacing(false);
        VerticalLayout right = new VerticalLayout(distanceLabel, paceLabel, elevationLabel); right.setPadding(false); right.setAlignItems(FlexComponent.Alignment.END);
        stats.add(left, right);

        // Map
        map = new OsmMapComponent();
        map.setSizeFull(); map.getStyle().set("min-height","55vh");

        // Controls
        startBtn = new Button("START", new Icon(VaadinIcon.PLAY), e-> startTracking());
        startBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        startBtn.getStyle().set("width","160px").set("height","56px").set("font-weight","800").set("font-size","1.2rem").set("border-radius","28px");

        pauseBtn = new Button("PAUSE", new Icon(VaadinIcon.PAUSE), e-> togglePause());
        pauseBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        pauseBtn.setVisible(false);
        pauseBtn.getStyle().set("width","120px").set("height","56px").set("border-radius","28px");

        stopBtn = new Button("STOP & SAVE", new Icon(VaadinIcon.STOP), e-> stopTracking());
        stopBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        stopBtn.setVisible(false);
        stopBtn.getStyle().set("width","160px").set("height","56px").set("font-weight","800").set("border-radius","28px");

        HorizontalLayout controls = new HorizontalLayout(startBtn, pauseBtn, stopBtn);
        controls.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        controls.setWidthFull(); controls.setPadding(true);

        add(stats, map, controls);
        setFlexGrow(1, map);

        // Inject live tracking JS
        getElement().executeJs("""
            window.runnaLive = {
                watchId: null,
                startTime: null,
                pausedTime: 0,
                lastPause: null,
                points: [],
                timerInterval: null,
                totalDist: 0,
                elevGain: 0,
                lastEle: null,
                
                haversine: function(lat1,lon1,lat2,lon2){
                    const R=6371000; const dLat=(lat2-lat1)*Math.PI/180; const dLon=(lon2-lon1)*Math.PI/180;
                    const a=Math.sin(dLat/2)**2 + Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*Math.sin(dLon/2)**2;
                    return R*2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
                },
                
                start: function(mapId){
                    this.points=[]; this.totalDist=0; this.elevGain=0; this.lastEle=null;
                    this.startTime=Date.now(); this.pausedTime=0; this.lastPause=null;
                    
                    const el=document.getElementById(mapId);
                    const dot=el.closest('.live-tracking-view')?.querySelector('[data-status-dot]');
                    
                    // Timer
                    this.timerInterval=setInterval(()=>{
                        const elapsed = Date.now() - this.startTime - this.pausedTime;
                        const h=Math.floor(elapsed/3600000), m=Math.floor((elapsed%3600000)/60000), s=Math.floor((elapsed%60000)/1000);
                        const txt = String(h).padStart(2,'0')+':'+String(m).padStart(2,'0')+':'+String(s).padStart(2,'0');
                        const timerEl = document.querySelector('h2'); if(timerEl) timerEl.textContent=txt;
                        el.dispatchEvent(new CustomEvent('timer-tick',{detail:{elapsed}}));
                    },1000);
                    
                    // GPS watch
                    if(!navigator.geolocation){ alert('GPS not supported'); return; }
                    this.watchId = navigator.geolocation.watchPosition(
                        pos=>{
                            const lat=pos.coords.latitude, lon=pos.coords.longitude, ele=pos.coords.altitude||0, acc=pos.coords.accuracy;
                            if(acc>30) return; // ignore low accuracy
                            
                            const mapEl=document.getElementById(mapId);
                            if(!mapEl || !mapEl._leafletMap) return;
                            
                            // distance
                            if(this.points.length>0){
                                const last=this.points[this.points.length-1];
                                const d=this.haversine(last[0],last[1],lat,lon);
                                if(d<3) return; // ignore jitter <3m
                                this.totalDist+=d;
                                if(this.lastEle!==null && ele>this.lastEle) this.elevGain+= (ele-this.lastEle);
                            }
                            this.lastEle=ele;
                            this.points.push([lat,lon,ele,Date.now()]);
                            
                            // Update UI
                            const distKm=(this.totalDist/1000).toFixed(2)+' km';
                            const elapsedSec=(Date.now()-this.startTime-this.pausedTime)/1000;
                            const paceSec = this.totalDist>0 ? elapsedSec/(this.totalDist/1000) : 0;
                            const paceTxt = paceSec>0 ? Math.floor(paceSec/60)+':'+String(Math.floor(paceSec%60)).padStart(2,'0')+' /km' : '-- /km';
                            
                            // Dispatch to Vaadin
                            mapEl.dispatchEvent(new CustomEvent('gps-update',{detail:{
                                lat,lon,ele,dist: this.totalDist, elevGain: this.elevGain, pace: paceTxt, distTxt: distKm, points: this.points
                            }}));
                            
                            // Draw on map
                            const lineCoords = this.points.map(p=>[p[0],p[1]]);
                            if(mapEl._leafletPolylines){ mapEl._leafletPolylines.forEach(l=>mapEl._leafletMap.removeLayer(l)); }
                            mapEl._leafletPolylines=[];
                            const line=L.polyline(lineCoords,{color:'#fc4c02',weight:5}).addTo(mapEl._leafletMap);
                            mapEl._leafletPolylines.push(line);
                            mapEl._leafletMap.setView([lat,lon], 16);
                            L.circleMarker([lat,lon],{radius:8,color:'white',fillColor:'#fc4c02',fillOpacity:1,weight:3}).addTo(mapEl._leafletMap);
                        },
                        err=>{ console.error(err); alert('GPS error: '+err.message); },
                        {enableHighAccuracy:true, maximumAge:0, timeout:10000}
                    );
                    
                    // Status dot green
                    const statusDot = document.querySelector('[data-status-dot]'); if(statusDot) statusDot.style.background='#00c853';
                },
                
                pause: function(){
                    if(this.lastPause===null){ this.lastPause=Date.now(); navigator.geolocation.clearWatch(this.watchId); }
                    else { this.pausedTime+=Date.now()-this.lastPause; this.lastPause=null; this.start(this.lastMapId); }
                },
                
                stop: function(mapId){
                    if(this.watchId!==null) navigator.geolocation.clearWatch(this.watchId);
                    if(this.timerInterval) clearInterval(this.timerInterval);
                    this.watchId=null;
                    return {
                        points: this.points,
                        distance: this.totalDist,
                        elevGain: this.elevGain,
                        duration: Date.now()-this.startTime-this.pausedTime
                    };
                },
                lastMapId: null
            };
        """);

        // Listen for GPS updates from JS
        map.getElement().addEventListener("gps-update", event -> {
            try {
                var detail = event.getEventData().getObject("event.detail");
                double dist = detail.getNumber("detail.dist");
                String distTxt = detail.getString("detail.distTxt");
                String paceTxt = detail.getString("detail.pace");
                double elev = detail.getNumber("detail.elevGain");
                
                getUI().ifPresent(ui -> ui.access(() -> {
                    distanceLabel.setText(distTxt);
                    paceLabel.setText(paceTxt);
                    elevationLabel.setText(String.format("%.0f m ↑", elev));
                }));
            } catch(Exception ex){ ex.printStackTrace(); }
        }).addEventData("event.detail.dist").addEventData("event.detail.distTxt")
          .addEventData("event.detail.pace").addEventData("event.detail.elevGain");
    }

    private void startTracking(){
        tracking=true; paused=false;
        startBtn.setVisible(false);
        pauseBtn.setVisible(true); pauseBtn.setText("PAUSE");
        stopBtn.setVisible(true);
        statusDot.getStyle().set("background","#00c853");
        getElement().executeJs("window.runnaLive.lastMapId=$0; window.runnaLive.start($0);", map.getId().get());
        com.vaadin.flow.component.notification.Notification.show("GPS tracking started - go run!");
    }

    private void togglePause(){
        paused=!paused;
        getElement().executeJs("window.runnaLive.pause();");
        if(paused){
            pauseBtn.setText("RESUME");
            pauseBtn.setIcon(new Icon(VaadinIcon.PLAY));
            statusDot.getStyle().set("background","#ffab00");
        } else {
            pauseBtn.setText("PAUSE");
            pauseBtn.setIcon(new Icon(VaadinIcon.PAUSE));
            statusDot.getStyle().set("background","#00c853");
        }
    }

    private void stopTracking(){
        getElement().executeJs("""
            const data = window.runnaLive.stop($0);
            const gpx = `<?xml version="1.0"?><gpx version="1.1" creator="RUNNA.WORLD"><trk><name>Live Run ${new Date().toISOString()}</name><type>RUN</type><trkseg>` + 
                data.points.map(p=>`<trkpt lat="${p[0]}" lon="${p[1]}"><ele>${p[2]}</ele><time>${new Date(p[3]).toISOString()}</time></trkpt>`).join('') + 
                `</trkseg></trk></gpx>`;
            $1.$server.saveGpx(gpx, data.distance, data.elevGain, data.duration);
        """, map.getId().get(), getElement());
        tracking=false;
        com.vaadin.flow.component.notification.Notification.show("Saving run...");
    }

    @com.vaadin.flow.component.ClientCallable
    public void saveGpx(String gpxContent, double distance, double elevGain, double durationMs){
        try {
            // Convert GPX string to MultipartFile and save via service
            byte[] bytes = gpxContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            var file = new world.runna.util.InMemoryMultipartFile("live-run-"+System.currentTimeMillis()+".gpx", bytes);
            // Inject service via Vaadin - get from Spring context
            var ctx = com.vaadin.flow.spring.SpringVaadinSession.getCurrent().getAttribute(org.springframework.context.ApplicationContext.class);
            // Fallback: use UI's spring
            var service = getUI().get().getSession().getAttribute(world.runna.service.ActivityService.class);
            if(service==null){
                // Get from Spring
                org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(
                    ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext()
                ).getBean(world.runna.service.ActivityService.class).createFromGpx(file, "Live Run - Lisbon "+java.time.LocalDate.now(), "RUN");
            } else {
                service.createFromGpx(file, "Live Run - Lisbon "+java.time.LocalDate.now(), "RUN");
            }
            com.vaadin.flow.component.notification.Notification.show("Run saved! Distance: "+String.format("%.2f km", distance/1000));
            getUI().ifPresent(ui-> ui.navigate(""));
        } catch(Exception e){
            e.printStackTrace();
            com.vaadin.flow.component.notification.Notification.show("Error saving: "+e.getMessage());
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent){
        super.onAttach(attachEvent);
        // Mark status dot for JS selector
        statusDot.getElement().setAttribute("data-status-dot","true");
    }
}
