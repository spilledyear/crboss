package com.lxy.tools.crboss.service;

import com.intellij.openapi.project.Project;
import com.lxy.tools.crboss.service.APIService;
import com.lxy.tools.crboss.utils.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class APIManager {
    private static final Map<Project, APIService> API_CACHE = new HashMap<>();

//    private final String PROJECT_PATH = "duinfr/cloud/poizon-cloud";

    public static synchronized void registerAPI(Project project) {
        if (project == null) {
            return;
        }

        API_CACHE.remove(project);

        String projectPath = parseProjectPath(project.getBasePath());
        API_CACHE.put(project, new APIService(projectPath));
    }

    public static synchronized void unRegisterAPI(Project project) {
        if (project == null) {
            return;
        }
        APIService apiService = API_CACHE.remove(project);
        if (apiService != null) {
            apiService.close();
        }
    }

    public static APIService getAPI(Project project) {
        if (project == null) {
            return null;
        }

        APIService apiService = API_CACHE.get(project);
        if (apiService == null) {
            registerAPI(project);
        }
        return API_CACHE.get(project);
    }

    private static String parseProjectPath(String basePath) {
        final String REMOTE_PREFIX = "[remote \"origin\"]", SSH = "git@", HTTPS = "https://", GIT = ".git";

        String projectPath = "";
        File file = new File(basePath, ".git" + File.separator + "config");
        if (!file.exists()) {
            System.err.println("无效Git目标，Git Config文件不存在： " + file.getPath());
            return projectPath;
        }

        String line = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            while ((line = br.readLine()) != null) {
                if (REMOTE_PREFIX.equals(line.trim())) {
                    line = br.readLine();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("解析文件异常： " + file.getPath());
            return projectPath;
        }

        if (line == null) {
            System.err.println("无效Git目标： " + file.getPath());
            return projectPath;
        }

        if (line.contains(SSH)) {
            projectPath = line.split(":")[1];
        } else if (line.contains(HTTPS)) {
            line = line.trim().replace(HTTPS, "");
            int index = line.indexOf("/");
            projectPath = line.substring(index + 1);
        } else {
            System.err.println("无效Git目标： " + file.getPath());
        }
        projectPath = projectPath.replace(GIT, "");
        System.out.println("GIT项目路径：" + projectPath);
        return projectPath;
    }
}
