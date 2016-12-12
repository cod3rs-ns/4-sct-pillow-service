package rs.acs.uns.sw.sct.constants;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface CompanyConstants {

    int PAGE_SIZE = 2;
    int DB_COUNT_COMPANIES = 3;

    Long ID = 1L;
    Long REMOVE_ID = 2L;

    String NAME = "company";
    String ADDRESS = "Trg Dositeja Obradovića";
    String PHONE_NUMBER = "5461963";

    String NEW_NAME = "company_new";
    String NEW_ADDRESS = "Trg Dositeja Obradovića 15";
    String NEW_PHONE_NUMBER = "5461963456";

    String UPDATED_NAME = "company_updated";
    String UPDATED_ADDRESS = "Trg Kralja Petra";
    String UPDATED_PHONE_NUMBER = "1239555";

    Pageable PAGEABLE = new PageRequest(0, 10);
}
