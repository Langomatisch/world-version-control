package de.langomatisch.wvc.git.action;

import de.langomatisch.wvc.WVCPlugin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GitContext {

    private WVCPlugin plugin;

}
