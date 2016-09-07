package youngfriend.utils.fileupload;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.common.util.StringUtils;
import youngfriend.gui.InputDlg;
import youngfriend.utils.PubUtil;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by xiong on 9/5/16.
 */
public class DocDelegate {
    private final JButton uploadBtn;
    private final JButton downloadBtn;
    private static final Logger logger = LoggerFactory.getLogger(DocDelegate.class);
    private JFileChooser fileUploadChooser = new JFileChooser();
    //3.0地址
    private String webUrl = null;
    private int serverPort = 12345;
    private JFileChooser fieldDownloadChooser = new JFileChooser();
    private String docId;
    private String docName;
    private File file;

    public DocDelegate(final JButton uploadBtn, final JButton downloadBtn) {
        fileUploadChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileUploadChooser.setMultiSelectionEnabled(false);
        fieldDownloadChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fieldDownloadChooser.setMultiSelectionEnabled(false);
        this.uploadBtn = uploadBtn;
        this.downloadBtn = downloadBtn;

        uploadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //第一次,提问web地址
                if (webUrl == null) {
                    if (!initUploadFileWebUrl()) {
                        //no webUrl
                        return;
                    }
                }
                int option = fileUploadChooser.showOpenDialog(PubUtil.mainFrame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    //确定后只缓存文件,保存组件是才上传
                    file = fileUploadChooser.getSelectedFile();
                    docName = file.getName();
                    uploadBtn.setText("(以设)重选上传文档");
                }
            }

            private boolean initUploadFileWebUrl() {
                //测试web地址有没有文件的web server
                InputDlg inputDlg = new InputDlg(PubUtil.mainFrame, "上传到web服务器地址", PubUtil.getUrlWeb(PubUtil.serviceConfig.getPro("url")));
                if (!inputDlg.isOk()) {
                    //  取消操作
                    return false;
                }
                boolean flag;
                String tempUrl;
                do {
                    tempUrl = inputDlg.getTxt();
                    flag = DocUtils.testExitFileService(tempUrl);
                    if (!flag) {
                        PubUtil.showMsg("web地址有误,请确认该地址有文件服务");
                        inputDlg.setVisible(true);
                    }
                    if (!inputDlg.isOk()) {
                        return false;
                    }
                } while (!flag);
                // only success change webUrl value
                webUrl = tempUrl;
                return true;
            }
        });
        downloadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int option = fieldDownloadChooser.showSaveDialog(PubUtil.mainFrame);
                if (option == JFileChooser.APPROVE_OPTION) {

                    try {
                        File dir = fieldDownloadChooser.getSelectedFile();
                        file = DocUtils.DownFileNew(webUrl, serverPort, docId, dir, docName);
                        PubUtil.showMsg("已下载");
                    } catch (Exception e) {
                        PubUtil.showMsg("下载出错");
                        logger.error(e.getMessage());
                    }
                }

            }
        });
    }

    public void load(JsonObject jsonData) {
        docId = PubUtil.getProp(jsonData, "docId");
        docName = PubUtil.getProp(jsonData, "docName");
        file = null;//only upload a new file have a value
        if (StringUtils.nullOrBlank(docId)) {
            downloadBtn.setText("上传组件文档");
            downloadBtn.setVisible(false);
        } else {
            uploadBtn.setText("重新上传");
            downloadBtn.setText("下载" + docName);
            downloadBtn.setVisible(true);
        }
    }

    public void save(JsonObject jsonData) throws IOException {
        if (file != null) {
            //file not null uplaod a new File ,else original file
            try {
                docId = DocUtils.uploadFile(webUrl, file);
            } catch (IOException e) {
                throw new RuntimeException("保存组件文档错误:" + e.getMessage());
            }
        }

        jsonData.addProperty("docId", docId);
        jsonData.addProperty("docName", docName);

    }
}
