package ch.trivadis.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by amo on 26.09.14.
 */
@Configuration
@ComponentScan(basePackages = {"ch.trivadis.service","ch.trivadis.repository","ch.trivadis.verticles"})
public class SpringConfiguration {
}
