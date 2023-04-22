package de.langomatisch.wvc.git.action.create;

import org.kohsuke.github.GHCreateRepositoryBuilder;
import de.langomatisch.wvc.git.GitRepository;
import de.langomatisch.wvc.git.action.GitAction;

import java.io.IOException;

public class GitCreateAction extends GitAction<GitCreateContext, GitRepository> {

    /**
     * Creates a new Git repository
     *
     * @param context the context
     * @return the created repository or null if the repository already exists
     */
    @Override
    public GitRepository apply(GitCreateContext context) {
        GHCreateRepositoryBuilder repository = context.getPlugin().getGitHub().createRepository(context.getWorld().getName());
        try {
            return new GitRepository(context.getPlugin(), repository.create(), context.getWorld());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
