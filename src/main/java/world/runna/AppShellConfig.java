package world.runna;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@Push // <-- Move it HERE
@Theme(value = "runna-world") // keep your existing theme if you have one
@PWA(name = "Runna World", shortName = "Runna")
public class AppShellConfig implements AppShellConfigurator {
}