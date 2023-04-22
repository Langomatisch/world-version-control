package de.langomatisch.wvc.git;

import de.langomatisch.wvc.git.action.create.GitCreateAction;
import de.langomatisch.wvc.git.action.create.GitCreateContext;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GitWorldManager {

    @Getter
    public static GitWorldManager instance;

    public GitWorldManager() {
        instance = this;
    }

    public boolean isGitWorld(World world) {
        return false;
    }

    public GitRepository getRepository(World world) {
        return null;
    }

    public GitRepository createRepository(World world) {
        GitCreateAction gitCreateAction = new GitCreateAction();
        return gitCreateAction.apply(
                new GitCreateContext(
                        world
                )
        );
    }

    private String getRepoName(World world) {
        return "wvc_" + world.getName();
    }
}
