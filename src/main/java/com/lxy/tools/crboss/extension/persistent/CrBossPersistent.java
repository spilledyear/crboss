package com.lxy.tools.crboss.extension.persistent;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "CrBoss", storages = {@Storage(value = "CrBoss.xml")})
public class CrBossPersistent implements PersistentStateComponent<CrBossPersistent> {

    public String gitlabUrl;

    public String gitlabToken;


    public static CrBossPersistent getInstance() {
        return ApplicationManager.getApplication().getService(CrBossPersistent.class);
    }


    @Override
    public @Nullable CrBossPersistent getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CrBossPersistent state) {
        this.gitlabUrl = state.gitlabUrl;
        this.gitlabToken = state.gitlabToken;
    }


    public String getGitlabUrl() {
        return gitlabUrl;
    }

    public void setGitlabUrl(String gitlabUrl) {
        this.gitlabUrl = gitlabUrl;
    }

    public String getGitlabToken() {
        return gitlabToken;
    }

    public void setGitlabToken(String gitlabToken) {
        this.gitlabToken = gitlabToken;
    }

}
