/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youngfriend;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.StringUtils;
import youngfriend.common.util.encoding.Base64;
import youngfriend.common.util.net.exception.ServiceInvokerException;
import youngfriend.exception.ParamValidateExcption;
import youngfriend.gui.InputDlg;
import youngfriend.gui.LoginDlg;
import youngfriend.gui.MaskDlg;
import youngfriend.gui.NewCatalogDlg;
import youngfriend.gui.NewModuleDlg;
import youngfriend.gui.SortCatalogDlg;
import youngfriend.gui.UpdateDialog;
import youngfriend.main_pnl.AbstractMainPnl;
import youngfriend.main_pnl.BtnModulePnl;
import youngfriend.main_pnl.CommonPnl;
import youngfriend.main_pnl.CommonUpdatePnl;
import youngfriend.main_pnl.ServicePnl;
import youngfriend.utils.Do4objs;
import youngfriend.utils.ModuleType;
import youngfriend.utils.PubUtil;
import youngfriend.utils.ServiceInvoker;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author xiong
 */
public class App extends javax.swing.JFrame {
    //TODO 分离 模块
    public static Border border = new LineBorder(Color.GRAY);
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private BeanDto moduleCatalogBean = null;
    private DefaultMutableTreeNode module_tree_root;
    private DefaultTreeModel module_tree_model;
    private boolean init = true;
    private BeanDto projectBean;
    private DefaultMutableTreeNode selectNode;
    private static final String[] CATALOG_TOSTRING = {"name", "code"};
    public static final String[] MODULE_TOSTRING = {"name", "code", "module_type"};
    private AbstractMainPnl mainPnl;

    private CommonPnl commonPnl = new CommonPnl();
    private ServicePnl servicePnl = new ServicePnl();
    private BtnModulePnl btnModulePnl = new BtnModulePnl();
    private CommonUpdatePnl commonUpdatePnl = new CommonUpdatePnl();
    private BeanDto moduleInfoBean;
    private List<BeanDto> servicebeans = null;

    private void clear() {
        this.moduleCatalogBean = null;
        this.moduleInfoBean = null;
        service_combo.setSelectedItem(null);
        moduleTypeLb.setText("");
        descTa.setText("");
        nameLb.setText("");
        searchTxt = null;
        search_tf.setText("");
        if (mainPnl != null) {
            mainPnl.clear();
        }
        PubUtil.enableComponents(oper_pnl, false);
        main_pnl.setVisible(false);
    }


    /**
     * Creates new form App
     */
    public App() {
        try {
            before();
            initComponents();
            after();
            launch();
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            PubUtil.showMsg("启动错误");
            System.exit(1);
        }
    }

    private JPopupMenu menu = new JPopupMenu("菜单");
    String searchTxt;
    List<Integer> searchIndexs = new ArrayList<Integer>();

    private void searchField(JTable fieldtable, int index_field) {
        String trim = search_tf.getText().trim();
        if (StringUtils.nullOrBlank(trim) || fieldtable.getRowCount() < 1) {
            return;
        }

        if (!trim.equals(searchTxt)) {
            searchIndexs.clear();
            searchTxt = trim;
            int row = fieldtable.getRowCount();
            for (int i = 0; i < row; i++) {
                String fieldStr = fieldtable.getValueAt(i, index_field).toString() + fieldtable.getValueAt(i, index_field + 1);
                if (fieldStr.indexOf(searchTxt) != -1) {
                    searchIndexs.add(i);
                }
            }
        }
        if (searchIndexs.isEmpty()) {
            PubUtil.showMsg("搜索结果为空");
            return;
        }
        int selectedRow = fieldtable.getSelectedRow();
        int newSelectRow = -1;
        for (int row : searchIndexs) {
            if (row > selectedRow) {
                newSelectRow = row;
                break;
            }
        }
        if (newSelectRow == -1) {
            newSelectRow = searchIndexs.get(0);
        }
        if (newSelectRow == selectedRow) {
            return;
        }
        fieldtable.setRowSelectionInterval(newSelectRow, newSelectRow);
        fieldtable.scrollRectToVisible(fieldtable.getCellRect(newSelectRow, 0, true));
        fieldtable.repaint();
    }

    public String getProjectid() {
        return projectBean.getValue("id");
    }

    private void searchModule() {
        String text = module_search_tf.getText().trim();
        if (StringUtils.nullOrBlank(text)) {
            return;
        }
        Enumeration<DefaultMutableTreeNode> enumeration = module_tree_root.breadthFirstEnumeration();
        if (selectNode != null) {
            while (enumeration.hasMoreElements()) {
                if (enumeration.nextElement().equals(selectNode)) {
                    break;
                }
            }
        }
        while (enumeration.hasMoreElements() || (enumeration = module_tree_root.breadthFirstEnumeration()) != null) {
            DefaultMutableTreeNode current = enumeration.nextElement();
            if (current.toString().contains(text)) {
                TreePath path = new TreePath(current.getPath());
                module_tree.setSelectionPath(path);
                module_tree.scrollPathToVisible(path);
                module_tree.repaint();
                break;
            }
        }

    }

    private void after() throws ServiceInvokerException, DocumentException {
        module_search_btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                searchModule();
            }
        });
        module_search_tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchModule();
                }
            }

        });

        search_btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                searchField(mainPnl.getTable(), 0);
            }
        });
        search_tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchField(mainPnl.getTable(), 0);
                }
            }

        });

        menu.setLabel("菜单");
        final JMenuItem saveItem = new JMenuItem("新增组件");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    List<String> codes = UpdateDialog.getSortCode(selectNode);
                    NewModuleDlg dlg = new NewModuleDlg(PubUtil.mainFrame);
                    dlg.showDlg(moduleCatalogBean, codes);
                    if (!dlg.isOk()) {
                        return;
                    }
                    BeanDto dto = dlg.getDto();
                    //排序
                    int index = UpdateDialog.getSortIndex(selectNode, dto.getValue("code"), codes);
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(dto, false);
                    selectNode.insert(node, index);
                    module_tree_model.reload(selectNode);
                    TreeNode[] pathToRoot = module_tree_model.getPathToRoot(node);
                    TreePath treePath = new TreePath(pathToRoot);
                    module_tree.setSelectionPath(treePath);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("保存失败");
                }
            }
        });
        final JMenuItem removeItem = new JMenuItem("删除组件");
        removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (moduleCatalogBean == null && moduleInfoBean == null) {
                    PubUtil.showMsg("请选择删除节点");
                    return;
                }
                if (!PubUtil.showConfirm(PubUtil.mainFrame, "确定删除吗?")) {
                    return;
                }
                try {
                    if (moduleInfoBean != null) {
                        ServiceInvoker.delModule(moduleInfoBean.getValue("id"));
                    } else {
                        ServiceInvoker.delCatalog(moduleCatalogBean.getValue("id"));
                    }
                    module_tree_model.removeNodeFromParent(selectNode);
                } catch (ServiceInvokerException e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("删除失败");
                }
            }
        });

        final JMenuItem addCatalogItem = new JMenuItem("新增目录");
        addCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (selectNode == null) {
                    PubUtil.showMsg("请选择目录");
                    return;
                }
                NewCatalogDlg newCatalogDlg = new NewCatalogDlg(PubUtil.mainFrame);
                java.util.List<String> codes = UpdateDialog.getSortCode(selectNode);
                newCatalogDlg.showDlg(moduleCatalogBean, codes);
                if (!newCatalogDlg.isOk()) {
                    return;
                }
                try {
                    JsonObject obj = newCatalogDlg.getObj();
                    String data = ServiceInvoker.saveCatalog(null, obj.get("name").getAsString(), obj.get("code").getAsString(),//
                            obj.get("description").getAsString(), obj.get("catalogalias").getAsString(), projectBean.getValue("id"), null);
                    JsonElement jsonElement = PubUtil.parseJson(data);
                    if (jsonElement == null) {
                        throw new RuntimeException("未知错误");
                    }
                    JsonArray asJsonArray = jsonElement.getAsJsonArray();
                    JsonObject jsonElement1 = asJsonArray.get(0).getAsJsonObject();
                    String id = jsonElement1.get("id").getAsString();
                    String code = jsonElement1.get("code").getAsString();

                    //排序
                    int index = UpdateDialog.getSortIndex(selectNode, code, codes);

                    obj.addProperty("id", id);
                    obj.addProperty("code", code);
                    BeanDto dto = new BeanDto(obj, CATALOG_TOSTRING);
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(dto, true);
                    selectNode.insert(node, index);
                    module_tree_model.reload(selectNode);
                    TreeNode[] pathToRoot = module_tree_model.getPathToRoot(node);
                    TreePath treePath = new TreePath(pathToRoot);
                    module_tree.setSelectionPath(treePath);
                    module_tree.expandPath(treePath);
                    module_tree.collapsePath(treePath);
                } catch (ServiceInvokerException e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("新增失败");
                }
            }
        });
        final JMenuItem removeCatalogItem = new JMenuItem("删除目录");
        removeCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (moduleCatalogBean == null) {
                    PubUtil.showMsg("请选择删除目录");
                    return;
                }
                if (selectNode.getChildCount() > 0) {
                    PubUtil.showMsg("请删除子节点");
                    return;
                }
                if (!PubUtil.showConfirm(PubUtil.mainFrame, "确定删除吗?")) {
                    return;
                }
                try {
                    ServiceInvoker.delCatalog(moduleCatalogBean.getValue("id"));
                    module_tree_model.removeNodeFromParent(selectNode);
                } catch (ServiceInvokerException e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("删除失败");
                }
            }
        });
        final JMenuItem editModuleItem = new JMenuItem("修改组件");
        editModuleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UpdateDialog dialog = new UpdateDialog(PubUtil.mainFrame, true);
                dialog.setNameValue(moduleCatalogBean.getValue("name"));
                if (moduleInfoBean != null) {
                    dialog.setDesc(moduleInfoBean.getValue("description"));
                } else {
                    dialog.setDesc(moduleCatalogBean.getValue("description"));
                }
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectNode.getParent();
                BeanDto parent_dto = (BeanDto) parentNode.getUserObject();
                List<String> codes = UpdateDialog.getSortCode(parentNode);
                String pre_code = moduleCatalogBean.getValue("code");
                codes.remove(pre_code);
                dialog.showDlg(parent_dto.getValue("code"), moduleCatalogBean, codes);
                if (!dialog.isOk()) {
                    return;
                }
                String nameValue = dialog.getNameValue();
                String desc = dialog.getDesc();
                String alias = dialog.getAlias();
                String code = dialog.getCode();
                try {
                    if (moduleInfoBean != null) {
                        JsonObject jsonData = getInputParamObj(moduleInfoBean);
                        if (jsonData != null) {
                            JsonObject inparam = jsonData.get("inparam").getAsJsonArray().get(0).getAsJsonObject();
                            if (!nameValue.equals(PubUtil.getProp(inparam, "label"))) {
                                inparam.addProperty("label", nameValue);
                            } else {
                                jsonData = null;
                            }
                        }
                        ServiceInvoker.saveModule(moduleInfoBean.getValue("id"), projectBean.getValue("id"), nameValue, desc, alias, jsonData == null ? null : jsonData.toString(), null, null);
                        moduleInfoBean.setItem("name", nameValue);
                        moduleInfoBean.setToString("name");
                        moduleInfoBean.setItem("description", desc);
                        moduleInfoBean.setItem("modulealias", alias);
                    }
                    ServiceInvoker.saveCatalog(moduleCatalogBean.getValue("id"), nameValue, code, desc, null, projectBean.getValue("id"), null);
                    moduleCatalogBean.setItem("name", nameValue);
                    moduleCatalogBean.setItem("code", code);
                    moduleCatalogBean.setToString(MODULE_TOSTRING);
                    if (!pre_code.equals(code)) {
                        int sortIndex = UpdateDialog.getSortIndex(parentNode, code, codes);
                        if (sortIndex > 0) {
                            sortIndex--;
                        }
                        parentNode.insert(selectNode, sortIndex);
                        module_tree.setSelectionPath(new TreePath(module_tree_model.getPathToRoot(selectNode)));
                        SwingUtilities.updateComponentTreeUI(module_tree);
                    } else {
                        module_tree_model.nodeChanged(selectNode);
                    }
                    reload_btnActionPerformed(null);
                } catch (ServiceInvokerException e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("保存失败");
                }
            }
        });
        final JMenuItem copyModuleItem = new JMenuItem("复制组件");

        copyModuleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String id = moduleCatalogBean.getValue("id");
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectNode.getParent();
                BeanDto parentDto = (BeanDto) parentNode.getUserObject();
                String parentCode = parentDto.getValue("code");
                try {
                    BeanDto dto = ServiceInvoker.moduleCopy(id, parentCode);
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(dto, false);
                    parentNode.add(node);
                    module_tree_model.nodeStructureChanged(parentNode);
                } catch (ServiceInvokerException e) {
                    PubUtil.showMsg("复制失败");
                    logger.error(e.getMessage(), e);
                }


            }
        });
        final JMenuItem editCatalogItem = new JMenuItem("修改目录");
        editCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UpdateDialog dialog = new UpdateDialog(PubUtil.mainFrame, false);
                dialog.setNameValue(moduleCatalogBean.getValue("name"));
                dialog.setDesc(moduleCatalogBean.getValue("description"));
                dialog.setAlias(moduleCatalogBean.getValue("catalogalias"));
                dialog.showDlg(null, moduleCatalogBean, null);
                if (!dialog.isOk()) {
                    return;
                }
                String nameValue = dialog.getNameValue();
                String desc = dialog.getDesc();
                String alias = dialog.getAlias();
                try {
                    ServiceInvoker.saveCatalog(moduleCatalogBean.getValue("id"), nameValue, null, desc, alias, projectBean.getValue("id"), null);
                    moduleCatalogBean.setItem("name", nameValue);
                    moduleCatalogBean.setItem("description", desc);
                    moduleCatalogBean.setItem("catalogalias", alias);
                    moduleCatalogBean.setToString(App.CATALOG_TOSTRING);
                    module_tree_model.nodeChanged(selectNode);
                } catch (ServiceInvokerException e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("保存失败");
                }
            }
        });

        final JMenuItem sortCatalogItem = new JMenuItem("移动节点");
        sortCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) module_tree.getLastSelectedPathComponent();
                BeanDto userObject = (BeanDto) sourceNode.getUserObject();
                SortCatalogDlg dlg = new SortCatalogDlg(PubUtil.mainFrame, module_tree, userObject);
                dlg.setVisible(true);
                if (!dlg.isOk()) {
                    return;
                }
                BeanDto selectDto = dlg.getSelectDto();
                if (selectDto == null) {
                    return;
                }
                try {
                    ServiceInvoker.sortCatalogTree(userObject.getValue("id"), selectDto.getValue("id"), dlg.getSortType());
                    reloadtree_btnActionPerformed(null);
                } catch (ServiceInvokerException e) {
                    PubUtil.showMsg("更新错误");
                    logger.error(e.getMessage());
                }
            }
        });
        menu.add(saveItem);
        menu.add(removeItem);
        menu.add(editModuleItem);
        menu.add(copyModuleItem);
        menu.addSeparator();
        menu.add(addCatalogItem);
        menu.add(removeCatalogItem);
        menu.add(editCatalogItem);
        menu.add(sortCatalogItem);
        project_combo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent
                                                 itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    projectSelect(projectBean = (BeanDto) project_combo.getSelectedItem());
                } else if (ItemEvent.DESELECTED == itemEvent.getStateChange()) {
                    projectSelect(null);
                }
            }
        });

        module_tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                clear();
                selectNode = (DefaultMutableTreeNode) module_tree.getLastSelectedPathComponent();
                if (selectNode != null && !selectNode.isRoot()) {
                    moduleCatalogBean = (BeanDto) selectNode.getUserObject();
                } else {
                    return;
                }
                if (moduleCatalogBean != null && !"true".equals(moduleCatalogBean.getValue("ismodule"))) {
                    PubUtil.enableComponents(oper_pnl, false);
                    main_pnl.setVisible(false);
                    return;
                }
                loadData();
            }
        });

        module_tree.setRootVisible(false);
        module_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        module_tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) module_tree.getLastSelectedPathComponent();
                    if (lastSelectedPathComponent == null || lastSelectedPathComponent.isRoot()) {
                        return;
                    }
                    BeanDto dto = (BeanDto) lastSelectedPathComponent.getUserObject();
                    boolean ismodule = "true".equals(dto.getValue("ismodule"));
                    saveItem.setEnabled(!ismodule);
                    addCatalogItem.setEnabled(!ismodule);
                    removeItem.setEnabled(ismodule);
                    removeCatalogItem.setEnabled(!ismodule);
                    editModuleItem.setEnabled(ismodule);
                    editCatalogItem.setEnabled(!ismodule);
                    copyModuleItem.setEnabled(ismodule);
                    menu.show(module_tree, mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });


        String msg = ServiceInvoker.designproject_project_get();
        JsonElement json = PubUtil.parseJson(msg);
        if (json != null) {
            JsonArray asJsonArray = json.getAsJsonArray();
            for (JsonElement obj : asJsonArray) {
                BeanDto dto = new BeanDto(obj.getAsJsonObject(), "name");
                if (dto.getValue("id").equals("6e37e3385fab44baa654f386b2b2030f")) {
                    project_combo.addItem(dto);
                }
            }
        }
    }


    private void projectSelect(final BeanDto projectBean) {
        this.projectBean = projectBean;
        module_tree_root.removeAllChildren();
        if (projectBean == null) {
            module_tree_model.reload(module_tree_root);
            return;
        }
        busyDoing(new Do4objs() {
            @Override
            public void do4ojbs(Object... objs) {
                try {
                    String jsonStr = ServiceInvoker.form_catalog_get(projectBean.getValue("id"));
                    addNodesLeftTree(jsonStr);
                    jsonStr = ServiceInvoker.btnmodule_catalog_get(projectBean.getValue("id"));
                    addNodesLeftTree(jsonStr);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException("建组件树失败");
                }

            }

        }, new Do4objs() {

            @Override
            public void do4ojbs(Object... ojbs) {
                module_tree_model.reload(module_tree_root);
                if (module_tree_root.getChildCount() > 0) {
                    module_tree.expandRow(0);
                }
            }
        });
    }

    private void addNodesLeftTree(String jsonStr) throws Exception {
        JsonElement jsonElement = PubUtil.parseJson(jsonStr);
        if (jsonElement != null) {
            JsonArray asJsonArray = jsonElement.getAsJsonArray();
            PubUtil.addTreeNode(module_tree_root, asJsonArray, "code", new Do4objs() {
                @Override
                public void do4ojbs(Object... objs) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) objs[0];
                    BeanDto dto = (BeanDto) objs[1];
                    String moduleid = dto.getValue("moduleid");
                    if (Strings.isNullOrEmpty(moduleid) || "#".equals(moduleid)) {
                        node.setAllowsChildren(true);
                        dto.setItem("ismodule", "false");
                    } else {
                        dto.setItem("ismodule", "true");
                        dto.setToString(MODULE_TOSTRING);
                    }
                }
            }, "name", "code");
        }
    }

    private void before() {
        PubUtil.mainFrame = this;
        module_tree_root = new DefaultMutableTreeNode("组件树");
        module_tree_model = new DefaultTreeModel(module_tree_root, true);
    }

    private void launch() throws Exception {
        this.pack();
        setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                closeApp();
            }
        });
        clear();
    }
    //

    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                tool = new javax.swing.JToolBar();
                jButton1 = new javax.swing.JButton();
                jButton2 = new javax.swing.JButton();
                reloadtree_btn = new javax.swing.JButton();
                edit_server2_btn = new javax.swing.JButton();
                bottom = new javax.swing.JPanel();
                jSplitPane2 = new javax.swing.JSplitPane();
                left = new javax.swing.JPanel();
                project_combo = new javax.swing.JComboBox();
                jPanel1 = new javax.swing.JPanel();
                jScrollPane3 = new javax.swing.JScrollPane();
                module_tree = new javax.swing.JTree();
                jPanel3 = new javax.swing.JPanel();
                module_search_tf = new javax.swing.JTextField();
                module_search_btn = new javax.swing.JButton();
                main = new javax.swing.JPanel();
                oper_pnl = new javax.swing.JPanel();
                save_btn = new javax.swing.JButton();
                jSeparator1 = new javax.swing.JSeparator();
                reload_btn = new javax.swing.JButton();
                main_pnl = new javax.swing.JPanel();
                jPanel2 = new javax.swing.JPanel();
                jLabel6 = new javax.swing.JLabel();
                jLabel1 = new javax.swing.JLabel();
                search_tf = new javax.swing.JTextField();
                search_btn = new javax.swing.JButton();
                jLabel2 = new javax.swing.JLabel();
                service_combo = new javax.swing.JComboBox();
                jLabel3 = new javax.swing.JLabel();
                nameLb = new javax.swing.JLabel();
                moduleTypeLb = new javax.swing.JLabel();
                jScrollPane1 = new javax.swing.JScrollPane();
                descTa = new javax.swing.JTextArea();
                jLabel4 = new javax.swing.JLabel();

                setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
                setTitle("数据源工具(简单版)");
                setSize(new java.awt.Dimension(600, 800));

                tool.setFloatable(false);
                tool.setRollover(true);

                jButton1.setText("退出");
                jButton1.setFocusable(false);
                jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                jButton1.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButton1ActionPerformed(evt);
                        }
                });
                tool.add(jButton1);

                jButton2.setText("重登录");
                jButton2.setFocusable(false);
                jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                jButton2.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButton2ActionPerformed(evt);
                        }
                });
                tool.add(jButton2);

                reloadtree_btn.setText("重新加载树");
                reloadtree_btn.setFocusable(false);
                reloadtree_btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                reloadtree_btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                reloadtree_btn.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                reloadtree_btnActionPerformed(evt);
                        }
                });
                tool.add(reloadtree_btn);

                edit_server2_btn.setText("修改2.0服务地址");
                edit_server2_btn.setFocusable(false);
                edit_server2_btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                edit_server2_btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                edit_server2_btn.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                edit_server2_btnActionPerformed(evt);
                        }
                });
                tool.add(edit_server2_btn);

                getContentPane().add(tool, java.awt.BorderLayout.PAGE_START);

                bottom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                bottom.setPreferredSize(new java.awt.Dimension(100, 10));
                getContentPane().add(bottom, java.awt.BorderLayout.PAGE_END);

                jSplitPane2.setDividerLocation(300);
                jSplitPane2.setOneTouchExpandable(true);

                left.setPreferredSize(new java.awt.Dimension(250, 642));
                left.setLayout(new java.awt.BorderLayout());

                left.add(project_combo, java.awt.BorderLayout.NORTH);

                jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                jPanel1.setLayout(new java.awt.BorderLayout());

                module_tree.setModel(module_tree_model);
                module_tree.setScrollsOnExpand(true);
                jScrollPane3.setViewportView(module_tree);

                jPanel1.add(jScrollPane3, java.awt.BorderLayout.CENTER);

                jPanel3.setPreferredSize(new java.awt.Dimension(298, 28));

                module_search_btn.setText("定位");

                javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap(187, Short.MAX_VALUE)
                                .addComponent(module_search_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(module_search_tf, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                        .addGap(56, 56, 56)))
                );
                jPanel3Layout.setVerticalGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(module_search_btn))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(module_search_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 1, Short.MAX_VALUE)))
                );

                jPanel1.add(jPanel3, java.awt.BorderLayout.NORTH);

                left.add(jPanel1, java.awt.BorderLayout.CENTER);

                jSplitPane2.setLeftComponent(left);

                main.setLayout(new java.awt.BorderLayout());

                oper_pnl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                oper_pnl.setPreferredSize(new java.awt.Dimension(656, 40));

                save_btn.setText("保存");
                save_btn.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                save_btnActionPerformed(evt);
                        }
                });

                reload_btn.setText("重新加载");
                reload_btn.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                reload_btnActionPerformed(evt);
                        }
                });

                javax.swing.GroupLayout oper_pnlLayout = new javax.swing.GroupLayout(oper_pnl);
                oper_pnl.setLayout(oper_pnlLayout);
                oper_pnlLayout.setHorizontalGroup(
                        oper_pnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(oper_pnlLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(save_btn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(reload_btn)
                                .addGap(291, 291, 291)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(338, Short.MAX_VALUE))
                );
                oper_pnlLayout.setVerticalGroup(
                        oper_pnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(oper_pnlLayout.createSequentialGroup()
                                .addGroup(oper_pnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(oper_pnlLayout.createSequentialGroup()
                                                .addGap(5, 5, 5)
                                                .addGroup(oper_pnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(save_btn)
                                                        .addComponent(reload_btn)))
                                        .addGroup(oper_pnlLayout.createSequentialGroup()
                                                .addGap(13, 13, 13)
                                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 2, Short.MAX_VALUE))
                );

                main.add(oper_pnl, java.awt.BorderLayout.NORTH);

                main_pnl.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
                main_pnl.setLayout(new java.awt.BorderLayout());

                jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jPanel2.setPreferredSize(new java.awt.Dimension(811, 100));

                jLabel6.setText("组件名称:");

                jLabel1.setText("字段搜索:");

                search_btn.setText("...");

                jLabel2.setText("服务名:");

                service_combo.addItemListener(new java.awt.event.ItemListener() {
                        public void itemStateChanged(java.awt.event.ItemEvent evt) {
                                service_comboItemStateChanged(evt);
                        }
                });

                jLabel3.setText("组件类型:");

                nameLb.setText("组件名值");

                moduleTypeLb.setText("类型值");

                descTa.setColumns(20);
                descTa.setLineWrap(true);
                descTa.setRows(5);
                jScrollPane1.setViewportView(descTa);

                jLabel4.setText("组件描述");

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(search_tf, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(search_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(86, 86, 86))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(nameLb, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel3)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(moduleTypeLb))
                                                        .addComponent(service_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel4)
                                                .addGap(18, 18, 18)))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                                .addGap(27, 27, 27))
                );
                jPanel2Layout.setVerticalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                        .addComponent(jLabel3)
                                                        .addComponent(moduleTypeLb)
                                                        .addComponent(jLabel6)
                                                        .addComponent(nameLb))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                        .addComponent(jLabel2)
                                                        .addComponent(service_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel4))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                        .addComponent(jLabel1)
                                                        .addComponent(search_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(search_btn)))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                main_pnl.add(jPanel2, java.awt.BorderLayout.PAGE_START);

                main.add(main_pnl, java.awt.BorderLayout.CENTER);

                jSplitPane2.setRightComponent(main);

                getContentPane().add(jSplitPane2, java.awt.BorderLayout.CENTER);

                pack();
        }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        closeApp();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void closeApp() {
        try {
            if (PubUtil.showConfirm(PubUtil.mainFrame, "确定要退出程序吗？")) {
                PubUtil.releaseApp();
                System.exit(0);
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            System.gc();
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        reLogin();
    }//GEN-LAST:event_jButton2ActionPerformed


    private void reload_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reload_btnActionPerformed
        DefaultMutableTreeNode beforeNode = this.selectNode;
        module_tree.clearSelection();
        TreePath treePath = new TreePath(module_tree_model.getPathToRoot(beforeNode));
        module_tree.setSelectionPath(treePath);
    }//GEN-LAST:event_reload_btnActionPerformed


    private void reloadtree_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadtree_btnActionPerformed
        projectSelect(projectBean);
    }//GEN-LAST:event_reloadtree_btnActionPerformed


    private void edit_server2_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_server2_btnActionPerformed
        setServe2Url();
        if (isVersion2()) {
            reload_btnActionPerformed(null);
        }
    }//GEN-LAST:event_edit_server2_btnActionPerformed

    private void service_comboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_service_comboItemStateChanged
        if (init) {
            return;
        }

        if (evt.getStateChange() == ItemEvent.SELECTED) {
            busyDoing(new Do4objs() {
                @Override
                public void do4ojbs(Object... objs) {
                    PubUtil.enableComponents(oper_pnl, false);
                    try {
                        mainPnl.serviceSelect((BeanDto) service_combo.getSelectedItem());
                    } catch (Exception e) {
                        PubUtil.showMsg("服务切换错误" + e.getMessage());
                        logger.error(e.getMessage(), e);
                    }
                }
            }, new Do4objs() {
                @Override
                public void do4ojbs(Object... objs) {
                    PubUtil.enableComponents(oper_pnl, true);
                }
            });

        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            try {
                mainPnl.serviceSelect(null);
            } catch (Exception e) {
                PubUtil.showMsg("服务切换错误" + e.getMessage());
                logger.error(e.getMessage(), e);
            }

        }
    }//GEN-LAST:event_service_comboItemStateChanged


    private void save_btnActionPerformed(java.awt.event.ActionEvent evt) {
        if (moduleInfoBean != null && !Strings.isNullOrEmpty(moduleCatalogBean.getValue("module_type")) && moduleCatalogBean.getValue("module_type").indexOf("非通用组件") != -1) {
            if (!PubUtil.showConfirm(PubUtil.mainFrame, "非通用组件，确定修改？")) {
                return;
            }
        }
        if (!checkValidate()) {
            return;
        }
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("servicename", getService().getValue("name"));
        String modulename = getModuleName();
        try {
            mainPnl.saveParam(modulename, jsonData);
            String modulealias = mainPnl.getModuleAlias();
            if (moduleInfoBean == null) {
                String moduleid = ServiceInvoker.saveModule(null, moduleCatalogBean.getValue("projectcode"), //
                        getModuleName(), descTa.getText(), modulealias, jsonData.toString(), null, null);
                try {
                    ServiceInvoker.saveCatalog(moduleCatalogBean.getValue("id"), getModuleName(), null, nameLb.getText(),//
                            null, projectBean.getValue("id"), moduleid);
                } catch (ServiceInvokerException e) {
                    ServiceInvoker.delModule(moduleid);
                    Throwables.propagate(e);
                }

            } else {
                ServiceInvoker.saveModule(moduleInfoBean.getValue("id"), moduleCatalogBean.getValue("projectcode"), getModuleName(),//
                        descTa.getText(), null, jsonData.toString(), null, null);
            }
            PubUtil.showMsg("保存成功");
            updateTreeNode();
            reload_btnActionPerformed(null);
        } catch (ParamValidateExcption e) {
            PubUtil.showMsg(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            PubUtil.showMsg("保存错误");
        }

    }

    private void updateTreeNode() {
        moduleCatalogBean.setItem("name", getModuleName());
        moduleCatalogBean.removeItem("module_type");
        moduleCatalogBean.setToString(MODULE_TOSTRING);
        module_tree_model.nodeChanged(selectNode);
    }

    private String getModuleName() {
        return nameLb.getText().trim();
    }


    public String setServe2Url() {
        String serverurl = PubUtil.getService2Url() == null ? "" : PubUtil.getService2Url();
        final InputDlg dlg = new InputDlg(this, "2.0服务地址", serverurl) {
            @Override
            public boolean validateData(String txt) {
                if (StringUtils.nullOrBlank(txt)) {
                    return false;
                }
                return true;
            }
        };
        if (dlg.isOk()) {
            serverurl = dlg.getTxt();
        }
        //如果 服务beans为空 或者 当前服务不等于修改服务
        if (PubUtil.serviceBeans_2 == null || !serverurl.equals(PubUtil.getService2Url())) {
            try {
                servicebeans = ServiceInvoker.getServices2(serverurl);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException("获取服务报错" + e.getMessage());
            }
        }
        return serverurl;
    }


    //    {
    //        "tablename": "CORP",
    //            "servicename": "module2",
    //            "exceptivefields": "不需返回的字段 逗号分隔",
    //            "hiddenfields": "需要隐藏的字段 逗号分隔",
    //            "sortfields": "排序字段 逗号分隔",
    //            "inparam": [
    //        {
    //            "name": "CORP",
    //                "label": "集团信息",
    //                "inParamType": "definite",
    //                "inParams": [
    //            {
    //                "name": "corpid",
    //                    "label": "集团Id",
    //                    "defaultValue": ""
    //            },
    //            {
    //                "name": "corpcode",
    //                    "label": "集团代码",
    //                    "defaultValue": ""
    //            }
    //            ],
    //            "maxLevel": "20",
    //                "maxLength": "64",
    //                "rootName": "授权单位名称",
    //                "codeField": "corpcode",
    //                "nameField": "corpname",
    //                "codeInc": "1,2",
    //                "propertyDefine": {}
    //        }
    //        ]
    //    }

    private void setServerCombos(List<BeanDto> servicebeans) {
        service_combo.removeAllItems();
        service_combo.addItem(null);
        if (servicebeans != null) {
            for (BeanDto dto : servicebeans) {
                service_combo.addItem(dto);
            }
        }
    }

    private void loadData() {
        busyDoing(new Do4objs() {
                      @Override
                      public void do4ojbs(Object... obj1) {
                          try {
                              init = true;
                              PubUtil.enableComponents(oper_pnl, false);
                              String moudleid = moduleCatalogBean.getValue("moduleid");
                              String jsonData = ServiceInvoker.getModule(moudleid);
                              JsonElement jsonElement = PubUtil.parseJson(jsonData);
                              moduleInfoBean = null;
                              if (jsonElement != null) {
                                  JsonArray asJsonArray = jsonElement.getAsJsonArray();
                                  if (asJsonArray.size() > 0) {
                                      JsonObject moduleObj = asJsonArray.get(0).getAsJsonObject();
                                      moduleInfoBean = new BeanDto(moduleObj, "name");
                                  }
                              }

                              if (moduleInfoBean != null) {
                                  if (isVersion2()) {
                                      moduleCatalogBean.setItem("module_type", "(2.0服务)");
                                      moduleCatalogBean.setToString(MODULE_TOSTRING);
                                      module_tree_model.nodeChanged(selectNode);
                                      if (PubUtil.serviceBeans_2 == null) {
                                          setServe2Url();
                                      } else {
                                          servicebeans = PubUtil.serviceBeans_2;
                                      }
                                  } else {
                                      servicebeans = PubUtil.serviceBeans;
                                  }
                                  nameLb.setText(moduleInfoBean.getValue("name"));
                                  descTa.setText(moduleInfoBean.getValue("description"));
                                  JsonObject inparamObj = getInputParamObj(moduleInfoBean);
                                  if (inparamObj == null) {
                                      return;
                                  }
                                  ModuleType moduleType = getModuleType();
                                  moduleTypeLb.setText(moduleType.getName());
                                  String servicename = inparamObj.get("servicename").getAsString();
                                  setServerCombos(servicebeans);
                                  BeanDto seritem = PubUtil.getDto(servicebeans, "name", servicename);
                                  if (seritem == null) {
                                      return;
                                  }
                                  service_combo.setSelectedItem(seritem);
                                  getMainPnl(moduleType);
                                  mainPnl.serviceSelect(seritem);
                                  mainPnl.loadData(inparamObj);
                              }
                          } catch (Exception e) {
                              PubUtil.enableComponents(oper_pnl, true);
                              main_pnl.setVisible(true);
                              init = false;
                              throw new RuntimeException(e);
                          }
                      }
                  }, new Do4objs() {
                      @Override
                      public void do4ojbs(Object... objs) {
                          PubUtil.enableComponents(oper_pnl, true);
                          main_pnl.setVisible(true);
                          init = false;
                          if (moduleInfoBean != null) {
                              String callparam = moduleInfoBean.getValue("callparam");
                              if (StringUtils.nullOrBlank(callparam) || !(callparam.endsWith("commonsimple.do") || callparam.endsWith("customservicedatasource.do") || callparam.endsWith("commonbuttonevent.do"))) {
                                  moduleCatalogBean.setItem("module_type", "(不适合修改)");
                                  moduleCatalogBean.setToString(MODULE_TOSTRING);
                                  module_tree_model.nodeChanged(selectNode);
                                  throw new RuntimeException("此组件不适合修改:callparam=" + moduleInfoBean.getValue("callparam"));
                              }
                          }
                      }
                  }

        );
    }

    private JsonObject getInputParamObj(BeanDto moduleInfoBean) {
        String inparam = moduleInfoBean.getValue("inparam");
        if (!Strings.isNullOrEmpty(inparam)) {
            inparam = new String(Base64.decode(inparam));
            JsonElement inparamEle = PubUtil.parseJson(inparam);
            if (inparamEle != null) {
                return inparamEle.getAsJsonObject();
            }
        }
        return null;
    }

    private BeanDto getService() {
        Object serviceBean = service_combo.getSelectedItem();
        return (BeanDto) serviceBean;
    }

    private boolean checkValidate() {
        if (moduleCatalogBean == null) {
            PubUtil.showMsg("组件为空");
            return false;
        }

        if (getService() == null) {
            PubUtil.showMsg("服务为空,请选择");
            return false;
        }
        return mainPnl.checkValidate();
    }


    private static void reLogin() {
        if (PubUtil.mainFrame != null) {
            PubUtil.mainFrame.setVisible(false);
        }
        LoginDlg loginDlg = new LoginDlg(null);
        if (!loginDlg.isOk()) {
            if (PubUtil.mainFrame != null) {
                PubUtil.mainFrame.setVisible(true);
            }
            return;
        }
        PubUtil.releaseApp();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                App app = new App();
                app.setVisible(true);
                app.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        reLogin();
    }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JPanel bottom;
        private javax.swing.JTextArea descTa;
        private javax.swing.JButton edit_server2_btn;
        private javax.swing.JButton jButton1;
        private javax.swing.JButton jButton2;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JLabel jLabel6;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JPanel jPanel3;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JScrollPane jScrollPane3;
        private javax.swing.JSeparator jSeparator1;
        private javax.swing.JSplitPane jSplitPane2;
        private javax.swing.JPanel left;
        private javax.swing.JPanel main;
        private javax.swing.JPanel main_pnl;
        private javax.swing.JLabel moduleTypeLb;
        private javax.swing.JButton module_search_btn;
        private javax.swing.JTextField module_search_tf;
        private javax.swing.JTree module_tree;
        private javax.swing.JLabel nameLb;
        private javax.swing.JPanel oper_pnl;
        private javax.swing.JComboBox project_combo;
        private javax.swing.JButton reload_btn;
        private javax.swing.JButton reloadtree_btn;
        private javax.swing.JButton save_btn;
        private javax.swing.JButton search_btn;
        private javax.swing.JTextField search_tf;
        private javax.swing.JComboBox service_combo;
        private javax.swing.JToolBar tool;
        // End of variables declaration//GEN-END:variables

    public static void busyDoing(final Do4objs backdo, final Do4objs enddo) {
        SwingWorker swingWorker = new SwingWorker<Void, Object>() {
            private boolean error = false;
            private String errorMsg;

            @Override
            protected Void doInBackground() {
                try {
                    backdo.do4ojbs();
                } catch (Throwable e) {
                    errorMsg = e.getMessage();
                    error = true;
                }
                return null;
            }

            @Override
            protected void done() {
                MaskDlg.unmask();
                if (error) {
                    PubUtil.showMsg(errorMsg);
                    error = false;
                    return;
                }
                if (enddo != null) {
                    try {
                        enddo.do4ojbs();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        PubUtil.showMsg(PubUtil.mainFrame, e.getMessage());
                    }
                }
            }
        };
        swingWorker.execute();
        MaskDlg.mask();
    }


    public ModuleType getModuleType() {
        String typee = moduleInfoBean.getValue("typee");
        if (StringUtils.nullOrBlank(typee)) {
            return ModuleType.COMMON;
        }
        ModuleType[] values = ModuleType.values();
        for (ModuleType value : values) {
            if (value.getValue().equals(typee)) {
                return value;
            }
        }
        return ModuleType.COMMON;
    }

    private void getMainPnl(ModuleType moduleType) {
        if (mainPnl != null) {
            main_pnl.remove(mainPnl);
            mainPnl = null;
        }

        switch (moduleType) {
            case COMMON:
                mainPnl = commonPnl;
                break;
            case SERVICE:
                mainPnl = servicePnl;
                break;
            case BUTTON:
                mainPnl = btnModulePnl;
                break;
            case COMMON_UPDATE:
                mainPnl = commonUpdatePnl;
                break;
        }
        if (mainPnl == null) {
            return;
        }
        main_pnl.add((Component) mainPnl, BorderLayout.CENTER);
        main_pnl.doLayout();
    }

    public BeanDto getModuneInfo() {
        return moduleInfoBean;
    }

    public boolean isVersion2() {
        if (moduleInfoBean == null) {
            return false;
        }
        return "2".equals(moduleInfoBean.getValue("typed"));
    }
}
