package de.langomatisch.wvc;

import de.langomatisch.wvc.config.GitConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.kohsuke.github.GitHub;

import java.io.IOException;

/*
    FIXME: we need a better name
 */
@Getter
@Plugin(name = "WVC", version = "1.0")
public class WVCPlugin extends JavaPlugin {

    @Getter
    private static WVCPlugin instance;

    private final GitConfig gitConfig = new GitConfig();

    private GitHub gitHub;

    @Override
    public void onEnable() {
        instance = this;
        try {
            gitHub = GitHub.connectUsingOAuth(gitConfig.getApiKey());
        } catch (IOException e) {
            System.out.println("Could not connect to GitHub");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
