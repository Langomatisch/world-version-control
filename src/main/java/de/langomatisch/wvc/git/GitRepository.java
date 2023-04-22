package de.langomatisch.wvc.git;

import de.langomatisch.wvc.WVCPlugin;
import lombok.Data;
import org.bukkit.World;
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

    private final WVCPlugin plugin;
    private final GHRepository repository;
    private final World world;
    private String branch;

    public GitRepository(WVCPlugin plugin, GHRepository repository, World world, String branch) {
        this.plugin = plugin;
        this.repository = repository;
        this.world = world;
        this.branch = branch;
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
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

    public GitRepository(WVCPlugin plugin, GHRepository repository, World world) {
        this(plugin, repository, world, "master");
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

}