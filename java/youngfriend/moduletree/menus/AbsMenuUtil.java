package youngfriend.moduletree.menus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.bean.BeanDto;
import youngfriend.moduletree.ModuleTreePnl;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Created by xiong on 9/20/16.
 */
public abstract class AbsMenuUtil {
    public AbsMenuUtil(ModuleTreePnl moduleTreePnl) {
        this.moduleTreePnl = moduleTreePnl;
        moduleTree = moduleTreePnl.getModuleTree();
        moduleTreeModel = (DefaultTreeModel) moduleTree.getModel();
        this.projectId = moduleTreePnl.getProjectId();
    }

    protected static final Logger logger = LoggerFactory.getLogger(AbsMenuUtil.class);
    protected ModuleTreePnl moduleTreePnl;
    protected DefaultTreeModel moduleTreeModel;
    protected JTree moduleTree;
    protected String projectId;
    protected DefaultMutableTreeNode selectNode;
    protected BeanDto moduleCatalogBean;
    protected BeanDto moduleInfoBean;


    public void updateSelectNode() {
        this.selectNode = moduleTreePnl.getSelectNode();
        this.moduleCatalogBean = moduleTreePnl.getModuleCatalogBean();
        this.moduleInfoBean = moduleTreePnl.getModuleInfoBean();
    }

}
