package com.togather.config.yaml;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(
        value = {
                "classpath:application-mysql.yml"
        },
        factory = YamlLoadFactory.class
)
public class YamlConfiguration {
}
