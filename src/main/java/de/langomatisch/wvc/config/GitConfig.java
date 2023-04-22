package de.langomatisch.wvc.config;

import lombok.Getter;

@Getter
public class GitConfig {

    private final String apiKey = System.getenv("GITHUB_API_KEY");

}
