package rs.acs.uns.sw.sct.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class DateFormatterUtil {

    @Bean(name = "dateFormatter")
    public SimpleDateFormat dateFormatter(){
        return new SimpleDateFormat("dd/MM/yyyy");
    }
}
