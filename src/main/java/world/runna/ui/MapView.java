package world.runna.ui;
import world.runna.service.ActivityService;
import world.runna.ui.components.OsmMapComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import java.util.stream.Collectors;

@Route(value="map", layout=MainLayout.class)
public class MapView extends HorizontalLayout {
    public MapView(ActivityService service){
        setSizeFull();
        OsmMapComponent map = new OsmMapComponent();
        map.setSizeFull();
        map.setCenter(39.479, -8.539, 13);
        var all = service.feed().stream()
            .filter(a->a.getRoute()!=null && !a.getRoute().isEmpty())
            .map(a->a.getRoute().stream().map(c->c[0]+","+c[1]).collect(Collectors.joining(";")))
            .collect(Collectors.toList());
        if(!all.isEmpty()) map.addRoutes(all);
        add(map);
    }
}
