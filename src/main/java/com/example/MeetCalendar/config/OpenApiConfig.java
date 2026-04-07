package com.example.MeetCalendar.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.COOKIE;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.APIKEY;

/**
 * OpenAPI/Swagger configuration for MeetCalendar REST API.
 * Defines API metadata and session-cookie security scheme for Swagger UI.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "API Система бронирования переговорок", version = "1.0.0")
)
@SecurityScheme(name = "session", type = APIKEY, in = COOKIE, paramName = "JSESSIONID")
public class OpenApiConfig {
}