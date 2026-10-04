package world.runna.ui;
import world.runna.dto.ActivityDTO;
import world.runna.service.ActivityService;
import world.runna.ui.components.OsmMapComponent;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.*;
import java.util.List;
import java.util.stream.Collectors;

@Route(value="", layout=MainLayout.class)
public class FeedView extends HorizontalLayout {
    public FeedView(ActivityService service){
        setSizeFull(); setPadding(true);
        VerticalLayout feed = new VerticalLayout(); feed.setWidth("420px"); feed.setPadding(false);
        H2 h = new H2("Your Feed"); h.getStyle().set("margin","0");
        feed.add(h);
        List<ActivityDTO> activities = service.feed();
        OsmMapComponent map = new OsmMapComponent();
        map.setSizeFull();
        if(activities.isEmpty()){
            feed.add(new Span("No activities yet - upload a GPX in Upload view"));
        }
        for(ActivityDTO a: activities){
            Div card = new Div();
            card.getStyle().set("background","white").set("border-radius","16px").set("padding","16px").set("box-shadow","0 2px 12px rgba(0,0,0,0.06)").set("cursor","pointer").set("width","100%");
            String km = String.format("%.2f km", a.getDistanceMeters()==null?0:a.getDistanceMeters()/1000);
            card.add(new H4(a.getTitle()), new Span((a.getSportType()==null?"RUN":a.getSportType())+" • "+km));
            card.addClickListener(e->{
                if(a.getRoute()!=null){
                    String pl = a.getRoute().stream().map(c->c[0]+","+c[1]).collect(Collectors.joining(";"));
                    map.setRoute(pl);
                }
            });
            feed.add(card);
        }
        add(feed, map); setFlexGrow(1, map);
    }
}
