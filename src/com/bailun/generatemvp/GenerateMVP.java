package com.bailun.generatemvp;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import gherkin.lexer.Da;

import java.io.*;

public class GenerateMVP extends AnAction {
    String mModuleName;
    String mPackageName;
    String mSceneName;
    GenerateType mSceneType;
    String mGenerateTargetBasePath;
    String mPluginPath = "/com/bailun/generatemvp/template/";

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project != null) {
            mGenerateTargetBasePath = project.getBasePath();
        }
        GenerateMvpForm generateMvpForm = new GenerateMvpForm((moduleName, packageName, senseName, type) -> {
            mModuleName = moduleName;
            mSceneName = senseName;
            mPackageName = packageName + "." + mSceneName.toLowerCase();
            mSceneType = type;
            FileType viewType = null;
            switch (type) {
                case Activity:
                    viewType = FileType.Activity;
                    break;
                case Fragment:
                    viewType = FileType.Fragment;
                    break;
            }
            createClassFiles(viewType);
            createClassFiles(FileType.Presenter);
            createClassFiles(FileType.Model);
            createClassFiles(FileType.Interfaces);
        });
        generateMvpForm.setVisible(true);
    }

    private void createClassFiles(FileType type) {
        String content;
        String fileName = "";
        String modulePath = getModulePathString();
        String packagePath = mPackageName.replace(".", "/");
        String suffix = "";
        String prefix = "";
        switch (type) {
            case Activity:
                fileName = "view/Activity.txt";
                modulePath = modulePath + "/" + packagePath + "/view/";
                suffix = "Activity";
                break;
            case Fragment:
                fileName = "view/Fragment.txt";
                modulePath = modulePath + "/" + packagePath + "/view/";
                suffix = "Fragment";
                break;
            case Presenter:
                fileName = "presenter/ImpPresenter.txt";
                modulePath = modulePath + "/" + packagePath + "/presenter/";
                suffix = "Presenter";
                prefix = "Imp";
                break;
            case Model:
                fileName = "model/ImpModel.txt";
                modulePath = modulePath + "/" + packagePath + "/model/";
                suffix = "Model";
                prefix = "Imp";
                break;
            case Interfaces:
                fileName = "interfaces/MVP.txt";
                modulePath = modulePath + "/" + packagePath + "/interfaces/";
                suffix = "MVP";
                break;
        }
        content = readTemplateFile(fileName);
        content = dealTemplateContent(content);
        DataUtils.writeToFile(content, modulePath, prefix + mSceneName + suffix + ".java");
    }

    private String readTemplateFile(String fileName) {
        InputStream in;
        in = this.getClass().getResourceAsStream(mPluginPath + fileName);
        String content = "";
        try {
            content = DataUtils.readStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 替换模板中字符
     *
     * @param content 内容
     * @return 返回内容
     */
    private String dealTemplateContent(String content) {
        content = content.replace("$packageName", mPackageName);
        content = content.replace("$sceneName", mSceneName);
        switch (mSceneType) {
            case Fragment:
                content = content.replace("$sceneType", "Fragment");
                break;
            case Activity:
                content = content.replace("$sceneType", "Activity");
                break;
        }
        return content;
    }

    private String getModulePathString() {
        return mGenerateTargetBasePath + "/" + mModuleName + "/" + "src" + "/" + "main" + "/" + "java" + "/";
    }
}
