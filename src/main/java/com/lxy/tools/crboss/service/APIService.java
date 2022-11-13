package com.lxy.tools.crboss.service;

import com.lxy.tools.crboss.extension.persistent.CrBossPersistent;
import com.lxy.tools.crboss.model.CreateMrRequest;
import com.lxy.tools.crboss.utils.StringUtil;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.MergeRequest;

import java.util.Collections;
import java.util.List;

public class APIService {

    private GitLabApi gitLabApi;
    private int projectId;


    public APIService(String projectPath) {
        try {
            String gitlabUrl = CrBossPersistent.getInstance().getGitlabUrl();
            String gitlabToken = CrBossPersistent.getInstance().getGitlabToken();
            if (StringUtil.isBlank(gitlabUrl) || StringUtil.isBlank(gitlabToken)) {
                throw new RuntimeException("gitlabUrl 和 gitlabToken 不能为空！");
            }

            GitLabApi gitLabApi = new GitLabApi(gitlabUrl, gitlabToken);
            this.gitLabApi = gitLabApi;
            this.projectId = gitLabApi.getProjectApi().getProject(projectPath, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE).getId();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    public void close() {
        if (gitLabApi != null) {
            gitLabApi.close();
        }
    }

    public List<MergeRequest> getMergeRequests() {
        try {
            return gitLabApi.getMergeRequestApi().getMergeRequests(projectId);
        } catch (Exception e) {
            System.err.println("getMergeRequests error: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<Branch> getBranches() {
        try {
            return gitLabApi.getRepositoryApi().getBranches(projectId);
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public MergeRequest createMergeRequest(CreateMrRequest request) throws GitLabApiException {
        return gitLabApi.getMergeRequestApi().createMergeRequest(projectId, request.getSource(), request.getTarget(), request.getTitle(), request.getDesc(), null);
    }

    public GitLabApi getGitLabApi() {
        return gitLabApi;
    }
}
