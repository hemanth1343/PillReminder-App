package com.pillreminder.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI pillReminderOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Pill Reminder API")
						.description("REST API for the Pill Reminder application – manage medications, "
								+ "schedules, reminders, and adherence stats.")
						.version("1.0.0")
						.contact(new Contact().name("Pill Reminder Team").email("support@pillreminder.com")))
				.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
				.components(new Components().addSecuritySchemes("Bearer Authentication",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
								.description("Provide your JWT token")));
	}
}
