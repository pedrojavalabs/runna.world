
package world.runna.config;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.Meta;
import org.springframework.stereotype.Component;
@Component
@PWA(name="RUNNA.WORLD", shortName="RUNNA", description="GPS Running Tracker", backgroundColor="#000000", themeColor="#fc4c02", display="standalone", offlinePath="offline.html")
@Theme("runna-world")
@Push
@Meta(name="viewport", content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no")
@Meta(name="theme-color", content="#fc4c02")
public class AppShell implements AppShellConfigurator {}
