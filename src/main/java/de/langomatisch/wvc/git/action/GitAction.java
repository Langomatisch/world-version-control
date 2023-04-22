package de.langomatisch.wvc.git.action;

public abstract class GitAction<Context extends GitContext, Response> {

    public abstract Response apply(Context context);

}
