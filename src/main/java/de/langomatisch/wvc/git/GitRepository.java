package de.langomatisch.wvc.git;

import com.google.common.base.Preconditions;
import de.langomatisch.wvc.WVCPlugin;
import de.langomatisch.wvc.git.action.create.GitCreateAction;
import de.langomatisch.wvc.git.action.create.GitCreateContext;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.kohsuke.github.GHRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Data
public class GitRepository {

    public static boolean isGitWorld(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        File worldFolder = world.getWorldFolder();
        if (Arrays.stream(Objects.requireNonNull(worldFolder.listFiles()))
                .noneMatch(file -> file.getName().equals(".git"))) {
            return false;
        }
        return true;
    }

    public static GitRepository pullFromWeb(String user, String repositoryName, String branch) throws IOException {
        WVCPlugin plugin = WVCPlugin.getInstance();
        GHRepository repository = plugin.getGitHub().getRepository(user + "/" + repositoryName);
        if (repository == null) {
            return null;
        }
        return new GitRepository(plugin, repository, branch);
    }

    public static GitRepository getRepository(World world) throws IOException {
        return getRepository(world, "master");
    }

    public static GitRepository getRepository(World world, String branch) throws IOException {
        if (isGitWorld(world)) {
            WVCPlugin plugin = WVCPlugin.getInstance();
            GHRepository repository = plugin.getGitHub().getRepository(world.getName());
            if (repository == null) {
                return null;
            }
            return new GitRepository(plugin, repository, branch);
        }
        return null;
    }

    public static GitRepository createRepository(World world) {
        GitCreateAction gitCreateAction = new GitCreateAction();
        return gitCreateAction.apply(
                new GitCreateContext(
                        world
                )
        );
    }

    private final WVCPlugin plugin;
    private final GHRepository repository;
    private World world;
    private String branch;

    public GitRepository(WVCPlugin plugin, GHRepository repository, String branch) {
        this.plugin = plugin;
        this.repository = repository;
        this.branch = branch;
        Preconditions.checkNotNull(repository, "Repository cannot be null");
        if (Arrays.stream(Objects.requireNonNull(Bukkit.getWorldContainer().listFiles())).noneMatch(file -> file.getName().equals(repository.getName()))) {
            cloneProject(repository.getHtmlUrl().toString());
            WorldCreator worldCreator = new WorldCreator(repository.getName());
            this.world = worldCreator.createWorld();
        } else {
            this.world = Bukkit.getWorld("wvc_" + repository.getName());
        }
        pull();
        File worldFolder = world.getWorldFolder();
        if (Arrays.stream(Objects.requireNonNull(worldFolder.listFiles()))
                .noneMatch(file -> file.getName().equals(".git"))) {
            initGit().whenComplete((unused, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                }
                System.out.println("Git repository initialized");
            });
        }
    }

    private void cloneProject(String url) {
        File worldFolder = Bukkit.getWorldContainer();
        try {
            Process start = new ProcessBuilder("git", "clone", url).directory(worldFolder).start();
            // send output to console
            InputStreamReader inputStreamReader = new InputStreamReader(start.getInputStream());
            int read;
            while ((read = inputStreamReader.read()) != -1) {
                System.out.print((char) read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GitRepository(WVCPlugin plugin, GHRepository repository) {
        this(plugin, repository, "master");
    }

    /**
     * Initializes the git repository
     *
     * @return a completable future
     */
    private CompletableFuture<Void> initGit() {
        return CompletableFuture.supplyAsync(() -> {
            File worldFolder = world.getWorldFolder();
            try {
                Process start = new ProcessBuilder("git", "init").directory(worldFolder).start();
                // send output to console
                InputStreamReader inputStreamReader = new InputStreamReader(start.getInputStream());
                int read;
                while ((read = inputStreamReader.read()) != -1) {
                    System.out.print((char) read);
                }
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Adds all files to the git repository
     *
     * @param message the commit message
     * @return a completable future
     */
    private CompletableFuture<Void> commit(String message) {
        return CompletableFuture.supplyAsync(() -> {
            File worldFolder = world.getWorldFolder();
            try {
                Process start = new ProcessBuilder("git", "commit", "-m", message)
                        .directory(worldFolder).start();
                // send output to console
                InputStreamReader inputStreamReader = new InputStreamReader(start.getInputStream());
                int read;
                while ((read = inputStreamReader.read()) != -1) {
                    System.out.print((char) read);
                }
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Adds all files to the git repository
     *
     * @param branch the branch to switch to
     * @return a completable future
     */
    public CompletableFuture<Void> switchBranch(String branch) {
        return CompletableFuture.supplyAsync(() -> {
            File worldFolder = world.getWorldFolder();
            try {
                Process start = new ProcessBuilder("git", "checkout", branch)
                        .directory(worldFolder).start();
                // send output to console
                InputStreamReader inputStreamReader = new InputStreamReader(start.getInputStream());
                int read;
                while ((read = inputStreamReader.read()) != -1) {
                    System.out.print((char) read);
                }
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Adds all files to the git repository
     *
     * @return a completable future
     */
    public Set<String> getBranches() {
        try {
            return repository.getBranches().keySet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds all files to the git repository
     *
     * @return the last commit id
     */
    public String getCommitId() {
        try {
            return repository.getBranch(branch).getSHA1();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Pulls the latest changes from the remote repository
     */
    public void pull() {
        // FIXME: we should unload the world before pulling otherwise the world will be corrupted
        File worldFolder = world.getWorldFolder();
        // FIXME: we have to make sure there are no changes in the world before pulling
        //        otherwise the pull will fail
        Bukkit.unloadWorld(world, false);
        try {
            Process start = new ProcessBuilder("git", "pull")
                    .directory(worldFolder).start();
            // send output to console
            InputStreamReader inputStreamReader = new InputStreamReader(start.getInputStream());
            int read;
            while ((read = inputStreamReader.read()) != -1) {
                System.out.print((char) read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bukkit.createWorld(new WorldCreator(world.getName()));
    }

}