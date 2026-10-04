package world.runna.service;
import world.runna.model.GpsPoint;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;
@Service
public class GpxParserService {
    public ParsedResult parse(InputStream is) throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(is);
        NodeList trkpts = doc.getElementsByTagName("trkpt");
        List<GpsPoint> points = new ArrayList<>();
        List<double[]> route = new ArrayList<>();
        double totalDist=0, totalEleGain=0; Double prevEle=null; GpsPoint prev=null;
        for(int i=0;i<trkpts.getLength();i++){
            Element el = (Element) trkpts.item(i);
            double lat = Double.parseDouble(el.getAttribute("lat"));
            double lon = Double.parseDouble(el.getAttribute("lon"));
            double ele = 0;
            NodeList eleN = el.getElementsByTagName("ele");
            if(eleN.getLength()>0) ele = Double.parseDouble(eleN.item(0).getTextContent());
            GpsPoint p = new GpsPoint();
            p.setLatitude(lat); p.setLongitude(lon); p.setElevation(ele);
            if(prev!=null){
                double d = haversine(prev.getLatitude(), prev.getLongitude(), lat, lon);
                totalDist+=d;
                if(prevEle!=null && ele>prevEle) totalEleGain+= (ele-prevEle);
            }
            prev=p; prevEle=ele;
            points.add(p);
            route.add(new double[]{lat,lon});
        }
        ParsedResult r = new ParsedResult();
        r.points=points; r.route=route; r.distance=totalDist; r.elevationGain=(int)totalEleGain;
        return r;
    }
    double haversine(double lat1,double lon1,double lat2,double lon2){
        double R=6371000; double dLat=Math.toRadians(lat2-lat1); double dLon=Math.toRadians(lon2-lon1);
        double a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return R*2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
    }
    public static class ParsedResult {
        public List<GpsPoint> points; public List<double[]> route; public double distance; public int elevationGain;
    }
}
