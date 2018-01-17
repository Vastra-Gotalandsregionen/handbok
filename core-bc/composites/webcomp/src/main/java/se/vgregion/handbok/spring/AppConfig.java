package se.vgregion.handbok.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Patrik Björk
 */
@Configuration
@EnableScheduling
@PropertySource(value = "file:${user.home}/.app/handbok/application.properties")
@Import(JpaConfig.class)
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

}
