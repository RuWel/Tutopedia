package com.tutopedia.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@PropertySources(
    {@PropertySource("tutorials.properties")}
)
public class AppProperties {
	@Value("${ws.seconds}")
	@Getter
    private int seconds;
}
