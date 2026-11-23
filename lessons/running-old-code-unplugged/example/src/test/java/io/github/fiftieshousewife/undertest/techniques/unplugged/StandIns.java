package io.github.fiftieshousewife.undertest.techniques.unplugged;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistrar;

/**
 * The test profile's stand-in that has to be started: WireMock, on this machine, answering for the courier's rates
 * from its mapping files in stand-ins/rates. Its address becomes the setting stand-ins.rates.url.
 */
@Configuration(proxyBeanMethods = false)
@Profile("test")
class StandIns {

    @Bean(destroyMethod = "stop")
    WireMockServer courierStandIn() {
        final WireMockServer courier = new WireMockServer(wireMockConfig().dynamicPort()
                .usingFilesUnderClasspath("stand-ins/rates"));
        courier.start();
        return courier;
    }

    @Bean
    DynamicPropertyRegistrar courierAddress(final WireMockServer courierStandIn) {
        return settings -> settings.add("stand-ins.rates.url", courierStandIn::baseUrl);
    }
}
