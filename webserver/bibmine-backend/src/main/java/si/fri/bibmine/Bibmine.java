package si.fri.bibmine;

import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.TimeZone;

@DeclareRoles({"user", "admin"})
@ApplicationPath("api")
public class Bibmine extends Application {
    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("### Default timezone is set to: " +  TimeZone.getDefault().getID());
    }
}
