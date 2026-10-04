package world.runna.ui;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
public class MainLayout extends AppLayout {
    public MainLayout(){
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("RUNNA.WORLD");
        title.getStyle().set("font-size","var(--lumo-font-size-l)").set("margin","0").set("color","#fc4c02").set("font-weight","800");
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Feed", FeedView.class));
        nav.addItem(new SideNavItem("Map Explorer", MapView.class));
        nav.addItem(new SideNavItem("Upload", UploadView.class));
        addToNavbar(toggle,title);
        addToDrawer(new Scroller(nav));
    }
}
