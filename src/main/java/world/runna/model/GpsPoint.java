package world.runna.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
public class GpsPoint {
    @Id @GeneratedValue private Long id;
    private Double latitude; private Double longitude; private Double elevation;
    private Instant timestamp; private Double heartRate; private Double speed;
    @ManyToOne @JoinColumn(name="activity_id") private Activity activity;
    public GpsPoint(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Double getLatitude(){return latitude;} public void setLatitude(Double v){this.latitude=v;}
    public Double getLongitude(){return longitude;} public void setLongitude(Double v){this.longitude=v;}
    public Double getElevation(){return elevation;} public void setElevation(Double v){this.elevation=v;}
    public Instant getTimestamp(){return timestamp;} public void setTimestamp(Instant v){this.timestamp=v;}
    public Double getHeartRate(){return heartRate;} public void setHeartRate(Double v){this.heartRate=v;}
    public Double getSpeed(){return speed;} public void setSpeed(Double v){this.speed=v;}
    public Activity getActivity(){return activity;} public void setActivity(Activity v){this.activity=v;}
}
