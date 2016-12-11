package rs.acs.uns.sw.sct.util;

import de.neuland.jade4j.Jade4J;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class MailUtil {

    public static String loadTemplate(String name){

        Map<String, Object> model = new HashMap<>();
        model.put("name", name);

        try {
            // Rendering html page for email
            String html = Jade4J.render("./src/main/resources/templates/mail-template.jade", model);
            return html;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
