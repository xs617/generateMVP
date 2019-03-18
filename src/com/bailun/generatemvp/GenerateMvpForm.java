package com.bailun.generatemvp;

import gherkin.deps.com.google.gson.Gson;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class GenerateMvpForm extends JDialog {
    private JPanel contentPanel;
    private JTextField moduleName;
    private JTextField packageName;
    private JTextField sceneName;
    private JRadioButton rbActivity;
    private JRadioButton rbFragment;
    private JButton buttonOK;
    private JButton buttonCancel;

    String cacheName = "cache";
    String pluginPath = "/com/bailun/generatemvp/";
    ;

    GenerateMvpForm(FormResultListener formResultListener) {
        setTitle("GenerateMvpForm");
        setContentPane(contentPanel);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(400, 300);
        setLocationRelativeTo(null);

        buttonOK.addActionListener(okActionListener -> onOk(formResultListener));
        buttonCancel.addActionListener(cancelActionListener -> onCancel());

        String cache = null;
        PersistenceCache persistenceCache = null;
        try {
            InputStream inputStream = new FileInputStream(pluginPath + cacheName);
            cache = DataUtils.readStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cache != null) {
            Gson gson = new Gson();
            persistenceCache = gson.fromJson(cache, PersistenceCache.class);
        }
        // 选择事件
        ButtonGroup generateType = new ButtonGroup();
        generateType.add(rbActivity);
        generateType.add(rbFragment);

        if (persistenceCache != null) {
            this.moduleName.setText(persistenceCache.getModuleName());
            this.packageName.setText(persistenceCache.getPackageName());
            this.sceneName.setText(persistenceCache.getSceneName());
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOk(FormResultListener formResultListener) {
        PersistenceCache persistenceCache = new PersistenceCache();
        persistenceCache.setModuleName(moduleName.getText().trim());
        persistenceCache.setPackageName(packageName.getText().trim());
        persistenceCache.setSceneName(sceneName.getText().trim());
        Gson gson = new Gson();
        String cache = gson.toJson(persistenceCache);
        DataUtils.writeToFile(cache, pluginPath, cacheName);
        formResultListener.onFormResult(moduleName.getText().trim(), packageName.getText().trim(), sceneName.getText().trim(), getGenerateType());
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private GenerateType getGenerateType() {
        if (rbFragment.isSelected()) {
            return GenerateType.Fragment;
        } else if (rbActivity.isSelected()) {
            return GenerateType.Activity;
        }
        return null;
    }

    public interface FormResultListener {
        void onFormResult(String moduleName, String packageName, String senseName, GenerateType type);
    }

    public static class PersistenceCache {

        private String moduleName;
        private String packageName;
        private String sceneName;

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }
    }
}
