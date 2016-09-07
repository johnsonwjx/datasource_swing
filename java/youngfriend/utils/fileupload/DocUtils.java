package youngfriend.utils.fileupload;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.common.util.StringUtils;
import youngfriend.common.util.encoding.Base64;
import youngfriend.utils.ServiceInvoker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by pengsheng on 16/9/1.
 */
public class DocUtils {
    private static final Logger logger = LoggerFactory.getLogger(DocUtils.class);

    /**
     * 测试V6Web地址 是否 有 file的action 即文件服务的 web 和 server
     *
     * @param httpUrl
     * @return
     */
    public static boolean testExitFileService(String httpUrl) {
        PostMethod post = null;
        boolean flag = false;
        try {
            post = new PostMethod(httpUrl + "/fileupload?uploadtype=transfer");
            HttpClient client = new HttpClient();
            int status = client.executeMethod(post);
            if (status == HttpStatus.SC_OK) {
                flag=true;
            }
            post.releaseConnection();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return flag;
    }

    /**
     * 将文件上传至项目系统
     *
     * @param localFile ： 本地文件
     * @return 项目系统文件ID
     * @throws IOException
     * @throws HttpException
     */
    public static String uploadFile(String httpUrl, File localFile) throws IOException {

        String newFileId = "";
        String fileName = localFile.getName();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        logger.info("从平台下载文件上传至文件服务 : " + localFile.getAbsolutePath() + ":::" + fileName);
        PostMethod post = new PostMethod(httpUrl + "/fileupload?uploadtype=transfer");
        Part[] parts = {
                new StringPart("fileid", ""),
                new StringPart("path", "defaultpath"),
                new StringPart("moduleid", "00000001"),
                new StringPart("isConver", "1"),
                new StringPart("topdf", ""),
                new StringPart("custompath", ""),
                new StringPart("type", fileType),
                new StringPart("encodingCode", "base64"),
                new StringPart("filelength", localFile.length() + ""),
                new FilePart("pdfdoc", read64(fileName), localFile)
        };

        MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
        post.setRequestEntity(entity);

        HttpClient client = new HttpClient();
        int status = client.executeMethod(post);
        if (status == HttpStatus.SC_OK) {
            String output = post.getResponseBodyAsString();
            int index = output.indexOf(";");
            if (index >= 0) {
                String result = output.substring(index + 1);
                newFileId = result.split("::")[1].split("\t")[0];
            }
        }
        post.releaseConnection();
        return newFileId;
    }

    private static String read64(String str) {
        if (str != null)
            str = Base64.encode(str.getBytes());
        return str;
    }

    /**
     * 将文件服务器上文件下载至应用服务器Tomcat
     *
     * @param fileId
     * @return
     * @throws Exception
     */
    public static File DownFileNew(String httpUrl, int port, String fileId, File dir, String fileName) throws Exception {
        MuFileNetUtil util = new MuFileNetUtil();
        FileOutputStream outstream = null;
        ByteArrayOutputStream bos = null;
        try {
            URL url = new URL(httpUrl);
            String data = "service:=file.downloadnew\n" + "fileid:=" + fileId
                    + "\n" + "moduleid:=\n" + "filename:=\n" + "path:=\n"
                    + "off:=" + 0 + "\n" + "len:=0";

            String ip = url.getHost();
            util.connect(ip, port);
            bos = new ByteArrayOutputStream();
            util.dodown(data, bos);
            if (StringUtils.nullOrBlank(fileName)) {
                fileName = fileId + "." + ServiceInvoker.getFileType(fileId);
            }
            File file = new File(dir, fileName);
            if (file.exists())
                file.delete();
            outstream = new FileOutputStream(file);
            bos.writeTo(outstream);
            return file;
        } catch (Exception e) {
            throw new Exception("下载文件出错,原因:" + e.getMessage());
        } finally {
            try {
                if (outstream != null) {
                    outstream.close();
                    bos.close();
                }
                if (util != null)
                    util.releaseAll();
            } catch (Exception e) {
            }
        }
    }
}


