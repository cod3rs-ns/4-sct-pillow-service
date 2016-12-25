package rs.acs.uns.sw.sct.conf;


import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.mock.env.MockPropertySource;

import static rs.acs.uns.sw.sct.constants.AnnouncementConstants.NEW_BASE_DIR;

public class PropertyMockingApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        MockPropertySource mockEnvVars = new MockPropertySource().withProperty("sct.file_upload.path", NEW_BASE_DIR);
        propertySources.addLast(mockEnvVars);
    }
}