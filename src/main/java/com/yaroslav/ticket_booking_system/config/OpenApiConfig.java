package com.yaroslav.ticket_booking_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ticket Booking System API")
                        .version("1.0")
                        .description(
                                "REST API for a ticket booking system. " +
                                "- Management of users, events, seats, tickets, orders, payments, and venues."
                        ));
    }
}
