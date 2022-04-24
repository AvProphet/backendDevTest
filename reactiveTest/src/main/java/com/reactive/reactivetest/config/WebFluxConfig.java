package com.reactive.reactivetest.config;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    @Bean
    public WebClient getWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(1))
                        .addHandlerLast(new WriteTimeoutHandler(1)));

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient.wiretap(true));

        return WebClient.builder()
                .clientConnector(connector)
                .build();
    }
}
