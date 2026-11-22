package io.github.fiftieshousewife.undertest.techniques.slices;

import io.github.fiftieshousewife.undertest.techniques.slices.fixed.DeliveryView;
import io.github.fiftieshousewife.undertest.techniques.slices.today.TodaysDeliveryView;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** The lesson's switch: today's answer with -Pcode=today, the fixed one otherwise. */
@Configuration(proxyBeanMethods = false)
public class AnswerChoice {

    @Bean
    @ConditionalOnProperty(name = "code", havingValue = "today")
    DeliveryAnswers todaysAnswers() {
        return TodaysDeliveryView::of;
    }

    @Bean
    @ConditionalOnProperty(name = "code", havingValue = "fixed", matchIfMissing = true)
    DeliveryAnswers fixedAnswers() {
        return DeliveryView::of;
    }
}
