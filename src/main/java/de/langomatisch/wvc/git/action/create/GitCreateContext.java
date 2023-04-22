package de.langomatisch.wvc.git.action.create;

import de.langomatisch.wvc.git.action.GitContext;
import lombok.*;
import org.bukkit.World;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GitCreateContext extends GitContext {

    private World world;

}
