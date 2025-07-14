package com.myapp.quiz.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.myapp.quiz.constants.Constants;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    private static final String QUIZ_APP = "QuizApp";
    private static final String PATH_TO_MATCH = "/**";

    @Bean
    public OpenAPI openAPI(@Value(Constants.API_TITLE) String title,
                           @Value(Constants.API_VERSION) String version,
                           @Value(Constants.API_DESCRIPTION) String description,
                           @Value(Constants.API_SERVER_URL) String serverUrl,
                           @Value(Constants.API_SERVER_NAME) String serverName) {
        return new OpenAPI().info(new Info()
                            .title(title)
                            .version(version)
                            .description(description))
                            .servers(List.of(new Server()
                            .url(serverUrl)
                            .description(serverName)))
                            .components(new Components()
                                    .addSecuritySchemes(Constants.API_AUTHORIZATION, new SecurityScheme()));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group(QUIZ_APP).pathsToMatch(PATH_TO_MATCH).build();
    }
}
