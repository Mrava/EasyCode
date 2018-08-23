package com.sjhy.plugin.ui;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiPackage;
import com.sjhy.plugin.constants.MsgValue;
import com.sjhy.plugin.entity.TableInfo;
import com.sjhy.plugin.entity.Template;
import com.sjhy.plugin.entity.TemplateGroup;
import com.sjhy.plugin.tool.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 选择保存路径
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
public class SelectSavePath extends JDialog {
    /**
     * 主面板
     */
    private JPanel contentPane;
    /**
     * 确认按钮
     */
    private JButton buttonOK;
    /**
     * 取消按钮
     */
    private JButton buttonCancel;
    /**
     * 模型下拉框
     */
    private JComboBox<String> moduleComboBox;
    /**
     * 包字段
     */
    private JTextField packageField;
    /**
     * 路径字段
     */
    private JTextField javaPathField;
    /**
     * 包选择按钮
     */
    private JButton packageChooseButton;
    /**
     * 路径选择按钮
     */
    private JButton javaPathChooseButton;
    /**
     * 模板全选框
     */
    private JCheckBox allCheckBox;
    /**
     * 模板面板
     */
    private JPanel javaTemplatePanel;
    /**
     * 统一配置复选框
     */
    private JCheckBox unifiedConfig;
    private JTextField htmlPathField;
    private JButton htmlPathChooseButton;
    private JPanel htmlTemplatePanel;
    /**
     * java所有模板复选框
     */
    private List<JCheckBox> javaCheckBoxList = new ArrayList<>();
    /**
     * html所有模板复选框
     */
    private List<JRadioButton> htmlRadioBoxList = new ArrayList<>();
    /**
     * 数据缓存工具类
     */
    private CacheDataUtils cacheDataUtils = CacheDataUtils.getInstance();
    /**
     * java模板组对象
     */
    private TemplateGroup javaTemplateGroup;
    /**
     * html模板组对象
     */
    private TemplateGroup htmlTemplateGroup;
    /**
     * 表信息工具
     */
    private TableInfoUtils tableInfoUtils = TableInfoUtils.getInstance();

    /**
     * 构造方法
     */
    public SelectSavePath() {
        ConfigInfo configInfo = ConfigInfo.getInstance();
        this.javaTemplateGroup = configInfo.getJavaTemplateGroupMap().get(configInfo.getCurrJavaTemplateGroupName());
        this.htmlTemplateGroup = configInfo.getHtmlTemplateGroupMap().get(configInfo.getCurrHtmlTemplateGroupName());

        init();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * 获取已经选中的模板
     *
     * @return 模板对象集合
     */
    private List<Template> getJavaSelectTemplate() {
        // 获取到已选择的复选框
        List<String> selectTemplateNameList = new ArrayList<>();
        javaCheckBoxList.forEach(jCheckBox -> {
            if (jCheckBox.isSelected()) {
                selectTemplateNameList.add(jCheckBox.getText());
            }
        });
        List<Template> selectTemplateList = new ArrayList<>(selectTemplateNameList.size());
        if (selectTemplateNameList.isEmpty()) {
            return selectTemplateList;
        }
        // 将复选框转换成对应的模板对象
        javaTemplateGroup.getElementList().forEach(template -> {
            if (selectTemplateNameList.contains(template.getName())) {
                selectTemplateList.add(template);
            }
        });
        return selectTemplateList;
    }

    /**
     * 获取一选中的html 模板
     *
     * @return
     */
    private List<Template> getHtmlSelectTemplate() {
        JRadioButton select = null;
        for (JRadioButton radioButton : htmlRadioBoxList) {
            if (radioButton.isSelected()) {
                select = radioButton;
                break;
            }
        }
        List<Template> selectTemplateList = new ArrayList<>();
        if (select == null) {
            return selectTemplateList;
        }
        for (Template template : htmlTemplateGroup.getElementList()) {
            if (select.getText().equals(template.getName())) {
                selectTemplateList.add(template);
            }
        }
        return selectTemplateList;
    }

    /**
     * 确认按钮回调事件
     */
    private void onOK() {
      /*  List<Template> selectJavaTemplateList = getJavaSelectTemplate();
        // 如果选择的模板是空的
        if (selectJavaTemplateList.isEmpty()) {
            Messages.showWarningDialog("Can't Select Java Template!", MsgValue.TITLE_INFO);
            return;
        }*/
        String javaSavePath = javaPathField.getText();
        if (StringUtils.isEmpty(javaSavePath)) {
            Messages.showWarningDialog("Can't Select Save Java Path!", MsgValue.TITLE_INFO);
            return;
        }
        String htmlSavePath = htmlPathField.getText();
        if (StringUtils.isEmpty(htmlSavePath)) {
            Messages.showWarningDialog("Can't Select Html Save Path!", MsgValue.TITLE_INFO);
            return;
        }
        // 设置好配置信息
        cacheDataUtils.setSavePath(javaSavePath);
        cacheDataUtils.setSelectTemplate(getJavaSelectTemplate());
        cacheDataUtils.setPackageName(packageField.getText());
        cacheDataUtils.setSelectModule(getSelectModule());
        cacheDataUtils.setUnifiedConfig(unifiedConfig.isSelected());
        cacheDataUtils.setHtmlSavePath(htmlSavePath);
        cacheDataUtils.setSelectHtmlTemplate(getHtmlSelectTemplate());
        // 生成代码
        VelocityUtils.getInstance().handler();
        // 关闭窗口
        dispose();
    }

    /**
     * 取消按钮回调事件
     */
    private void onCancel() {
        dispose();
    }

    /**
     * 初始化方法
     */
    private void init() {
        //添加模板组
        javaCheckBoxList.clear();
        javaTemplatePanel.setLayout(new GridLayout(6, 2));
        javaTemplateGroup.getElementList().forEach(template -> {
            JCheckBox checkBox = new JCheckBox(template.getName());
            javaCheckBoxList.add(checkBox);
            javaTemplatePanel.add(checkBox);
        });
        htmlRadioBoxList.clear();
        htmlTemplatePanel.setLayout(new GridLayout(4, 2));
        ButtonGroup group = new ButtonGroup();
        htmlTemplateGroup.getElementList().forEach(template -> {
            JRadioButton radioButton = new JRadioButton(template.getName());
            radioButton.setSelected(true);
            htmlRadioBoxList.add(radioButton);
            htmlTemplatePanel.add(radioButton);
            group.add(radioButton);
        });
        //添加全选事件
        allCheckBox.addActionListener(e -> javaCheckBoxList.forEach(jCheckBox -> jCheckBox.setSelected(allCheckBox.isSelected())));

        //初始化Module选择
        for (Module module : cacheDataUtils.getModules()) {
            moduleComboBox.addItem(module.getName());
        }

        //监听module选择事件
        moduleComboBox.addActionListener(e -> {
            // 刷新路径
            refreshPath();
        });

        //添加包选择事件
        packageChooseButton.addActionListener(e -> {
            PackageChooserDialog dialog = new PackageChooserDialog("Package Chooser", cacheDataUtils.getProject());
            dialog.show();
            PsiPackage psiPackage = dialog.getSelectedPackage();
            if (psiPackage != null) {
                packageField.setText(psiPackage.getQualifiedName());
                // 刷新路径
                refreshPath();
            }
        });

        //初始化路径
        refreshPath();

        //选择路径
        javaPathChooseButton.addActionListener(new PathChooseActionListener(javaPathField));

        //选择html 生成路径
        htmlPathChooseButton.addActionListener(new PathChooseActionListener(htmlPathField));

        // 获取选中的表信息（鼠标右键的那张表）
        TableInfo tableInfo = tableInfoUtils.handler(Collections.singletonList(cacheDataUtils.getSelectDbTable())).get(0);
        // 设置默认配置信息
        if (!StringUtils.isEmpty(tableInfo.getSaveModelName())) {
            moduleComboBox.setSelectedItem(tableInfo.getSaveModelName());
        }
        if (!StringUtils.isEmpty(tableInfo.getSavePackageName())) {
            packageField.setText(tableInfo.getSavePackageName());
        }
        javaPathField.setText(getSavePath(tableInfo.getSavePath()));
        htmlPathField.setText(getSavePath(tableInfo.getHtmlSavePath()));
    }

    /**
     * 获取默认选中的目录
     *
     * @param savePath
     * @return
     */
    private String getSavePath(String savePath) {
        if (!StringUtils.isEmpty(savePath)) {
            // 判断是否需要凭借项目路径
            if (savePath.startsWith("./")) {
                String projectPath = cacheDataUtils.getProject().getBasePath();
                savePath = projectPath + savePath.substring(1);
            }

        }
        return savePath;
    }

    /**
     * 获取选中的Module
     *
     * @return 选中的Module
     */
    private Module getSelectModule() {
        String name = (String) moduleComboBox.getSelectedItem();
        for (Module module : cacheDataUtils.getModules()) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        return cacheDataUtils.getModules()[0];
    }

    /**
     * 获取基本路径
     *
     * @return 基本路径
     */
    private String getBasePath() {
        Module module = getSelectModule();
        String baseDir = cacheDataUtils.getProject().getBasePath();
        if (module != null) {
            baseDir = new File(module.getModuleFilePath()).getParent();
        }
        // 针对Mac版路径做优化
        if (baseDir != null && baseDir.contains("/.idea")) {
            baseDir = baseDir.substring(0, baseDir.indexOf("/.idea"));
        }
        // 针对Maven项目
        File file = new File(baseDir + "/src/main/java");
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        // 针对普通Java项目
        file = new File(baseDir + "/src");
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return baseDir;
    }

    /**
     * 刷新目录
     */
    private void refreshPath() {
        String packageName = packageField.getText();
        // 获取基本路径
        String path = getBasePath();
        // 兼容Linux路径
        path = path.replace("\\", "/");
        // 如果存在包路径，添加包路径
        if (!StringUtils.isEmpty(packageName)) {
            path += "/" + packageName.replace(".", "/");
        }
        javaPathField.setText(path);
    }

    /**
     * 打开窗口
     */
    public void open() {
        this.pack();
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * 路径选择监听器
     */
    class PathChooseActionListener implements ActionListener {
        JTextField textField;

        public PathChooseActionListener(JTextField textField) {
            this.textField = textField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //将当前选中的model设置为基础路径
            VirtualFile path = cacheDataUtils.getProject().getBaseDir();
            Module module = getSelectModule();
            if (module != null) {
                path = VirtualFileManager.getInstance().findFileByUrl("file://" + new File(module.getModuleFilePath()).getParent());
            }
            VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), cacheDataUtils.getProject(), path);
            if (virtualFile != null) {
                textField.setText(virtualFile.getPath());
            }
        }
    }
}


