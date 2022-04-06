package com.test.back.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app")
public class HttpConfig {
    public static final class Endpoint {
        public static final String SIMILAR_IDS = "similar-ids";
        public static final String PRODUCT_URL = "product-url";

        private Endpoint() {
        }
    }
    @Getter(AccessLevel.NONE)
    private Map<String, String> endpointsMap = Collections.emptyMap();

    public String getUrl(String endpointName) {
        return endpointsMap.get(endpointName);
    }
}
