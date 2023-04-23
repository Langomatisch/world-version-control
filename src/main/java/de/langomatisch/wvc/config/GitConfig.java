package de.langomatisch.wvc.config;

import lombok.Getter;

@Getter
public class GitConfig {

    // FIXME: make this to a config file (git.json)

    private final String apiKey = System.getenv("GITHUB_API_KEY");

}
