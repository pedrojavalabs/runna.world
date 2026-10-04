
package world.runna.ui.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class OsmMapComponent extends Div {

    private boolean mapInitialized = false;
    private String pendingRoute = null;
    private List<String> pendingRoutes = null;
    private double pendingLat = 39.479;
    private double pendingLon = -8.539;
    private int pendingZoom = 13;

    public OsmMapComponent(){
        setId("osm-map-" + System.nanoTime());
        setSizeFull();
        getStyle().set("min-height","500px").set("border-radius","12px").set("overflow","hidden");
        // container for leaflet
        getElement().setProperty("innerHTML", "<div id='"+getId().get()+"-inner' style='width:100%;height:100%;min-height:500px;'></div>");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Load Leaflet CSS
        attachEvent.getUI().getPage().addStyleSheet("https://unpkg.com/leaflet@1.9.4/dist/leaflet.css");
        // Load Leaflet JS then init map
        attachEvent.getUI().getPage().addJavaScript("https://unpkg.com/leaflet@1.9.4/dist/leaflet.js");
        // Wait a bit for leaflet to load and init
        attachEvent.getUI().getPage().executeJs(
            "const check = setInterval(()=>{"+
            " if(window.L){"+
            "  clearInterval(check);"+
            "  const container = document.getElementById($0);"+
            "  if(!container) return;"+
            "  const innerId = $0 + '-inner';"+
            "  const inner = document.getElementById(innerId);"+
            "  if(!inner) return;"+
            "  const map = L.map(innerId).setView([$1,$2],$3);"+
            "  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{attribution:'© OpenStreetMap'}).addTo(map);"+
            "  container._leafletMap = map;"+
            "  container._leafletPolylines = [];"+
            "  if(container._pendingRoute){ container._leafletMapMethods.setRoute(container._pendingRoute); }"+
            "  if(container._pendingRoutes){ container._leafletMapMethods.addRoutes(container._pendingRoutes); }"+
            " }"+
            "}, 100);"
            + "if(!document.getElementById($0)._leafletMapMethods){"
            + " document.getElementById($0)._leafletMapMethods = {"
            + "  setRoute: function(polyline){"
            + "    const el = document.getElementById($0); const map = el._leafletMap; if(!map) { el._pendingRoute = polyline; return; }"
            + "    el._leafletPolylines.forEach(l=>map.removeLayer(l)); el._leafletPolylines=[];"
            + "    const coords = polyline.split(';').map(s=>{ const parts=s.split(','); return [parseFloat(parts[0]), parseFloat(parts[1])]; }).filter(c=>!isNaN(c[0]));"
            + "    if(coords.length==0) return; const line = L.polyline(coords,{color:'#fc4c02',weight:5}).addTo(map); el._leafletPolylines.push(line); map.fitBounds(line.getBounds(),{padding:[20,20]});"
            + "  },"
            + "  addRoutes: function(json){"
            + "    const el = document.getElementById($0); const map = el._leafletMap; if(!map){ el._pendingRoutes=json; return; }"
            + "    try{ const all = JSON.parse(json); all.forEach(pl=>{ const coords = pl.split(';').map(s=>{ const parts=s.split(','); return [parseFloat(parts[0]), parseFloat(parts[1])]; }); const line = L.polyline(coords,{color:'#fc4c02',weight:3,opacity:0.6}).addTo(map); el._leafletPolylines.push(line); }); }catch(e){ console.error(e); }"
            + "  },"
            + "  setCenter: function(lat,lon,zoom){ const el=document.getElementById($0); const map=el._leafletMap; if(map) map.setView([lat,lon],zoom); }"
            + " };"
            + "}",
            getId().get(), pendingLat, pendingLon, pendingZoom
        );
    }

    public void setRoute(String polyline){
        if(polyline==null) return;
        getElement().callJsFunction("eval", "this._leafletMapMethods && this._leafletMapMethods.setRoute('"+polyline.replace("'", "\'")+"')");
        // fallback via executeJs
        getUI().ifPresent(ui -> ui.getPage().executeJs("const el=document.getElementById($0); if(el && el._leafletMapMethods) el._leafletMapMethods.setRoute($1); else if(el) el._pendingRoute=$1;", getId().get(), polyline));
    }

    public void setCenter(double lat, double lon, int zoom){
        this.pendingLat=lat; this.pendingLon=lon; this.pendingZoom=zoom;
        getUI().ifPresent(ui -> ui.getPage().executeJs("const el=document.getElementById($0); if(el && el._leafletMapMethods) el._leafletMapMethods.setCenter($1,$2,$3);", getId().get(), lat, lon, zoom));
    }

    public void addRoutes(List<String> polylines){
        try {
            String json = new ObjectMapper().writeValueAsString(polylines);
            getUI().ifPresent(ui -> ui.getPage().executeJs("const el=document.getElementById($0); if(el && el._leafletMapMethods) el._leafletMapMethods.addRoutes($1); else if(el) el._pendingRoutes=$1;", getId().get(), json));
        } catch (Exception e){ e.printStackTrace(); }
    }
}
