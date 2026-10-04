package world.runna.ui;
import world.runna.service.ActivityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;

@Route(value="upload", layout=MainLayout.class)
public class UploadView extends VerticalLayout {
    public UploadView(ActivityService service){
        setSizeFull(); setAlignItems(Alignment.CENTER);
        H2 title = new H2("Upload GPX");
        TextField nameField = new TextField("Activity Title"); nameField.setValue("Morning Run - Torres Novas");
        ComboBox<String> sport = new ComboBox<>("Sport"); sport.setItems("RUN","RIDE","HIKE","WALK"); sport.setValue("RUN");
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer); upload.setAcceptedFileTypes(".gpx");
        Button save = new Button("Save Activity", e->{
            try{
                byte[] bytes = buffer.getInputStream().readAllBytes();
                var file = new world.runna.util.InMemoryMultipartFile(buffer.getFileName(), bytes);
                service.createFromGpx(file, nameField.getValue(), sport.getValue());
                Notification.show("Activity saved! Go to Feed.");
            }catch(Exception ex){ Notification.show("Error: "+ex.getMessage()); ex.printStackTrace(); }
        });
        add(title, nameField, sport, upload, save);
    }
}
