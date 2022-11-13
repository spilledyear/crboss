package com.lxy.tools.crboss.service;

import com.lxy.tools.crboss.extension.persistent.CrBossPersistent;
import com.lxy.tools.crboss.model.CreateMrRequest;
import com.lxy.tools.crboss.utils.StringUtil;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;

import java.util.Collections;
import java.util.List;

public class GitAPI {
    private static GitLabApi API;

    private static final String PROJECT_PATH = "duinfr/cloud/poizon-cloud";

    private static int PROJECT_ID;

    private static synchronized GitLabApi getInstance() {
        if (API == null) {
            new GitAPI();
        }
        return API;
    }

    private GitAPI() {
        try {
            String gitlabUrl = CrBossPersistent.getInstance().getGitlabUrl();
            String gitlabToken = CrBossPersistent.getInstance().getGitlabToken();
            if (StringUtil.isBlank(gitlabUrl) || StringUtil.isBlank(gitlabToken)) {
                throw new RuntimeException("gitlabUrl 和 gitlabToken 不能为空！");
            }

            API = new GitLabApi(gitlabUrl, gitlabToken);

            Project project = API.getProjectApi().getProject(PROJECT_PATH, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
            PROJECT_ID = project.getId();
        } catch (Exception e) {
            API = null;
            System.err.println("error: " + e.getMessage());
        }
    }

    public static List<MergeRequest> getMergeRequests() {
        try {
            return getInstance().getMergeRequestApi().getMergeRequests(PROJECT_ID);
        } catch (Exception e) {
            System.err.println("getMergeRequests error: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static List<Branch> getBranches() {
        try {
            return getInstance().getRepositoryApi().getBranches(PROJECT_ID);
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static MergeRequest createMergeRequest(CreateMrRequest request) throws GitLabApiException {
        return getInstance().getMergeRequestApi().createMergeRequest(PROJECT_ID, request.getSource(), request.getTarget(), request.getTitle(), request.getDesc(), null);
    }
}
