package youngfriend.utils.fileupload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import youngfriend.common.util.encoding.Base64;
import youngfriend.common.util.net.ServiceInvokerUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MuFileNetUtil {

    private Socket client = null;

    static Log log = LogFactory.getLog(MuFileNetUtil.class);

    public void connect(String addrstr, int port) throws Exception {
        InetAddress addr = InetAddress.getByName(addrstr);
        client = new Socket(addr, port);

    }

    public void connect() throws Exception {
        String url = System.getProperty("update");
        String port = "";
        String ip = url.substring("http://".length(), url.indexOf(":",
                "http://".length()));
        port = url.substring(url.indexOf(":", "http://".length()) + 1);
        int portInt = Integer.parseInt(port);
        connect(ip, portInt);
    }


    public void dodown(String s, OutputStream bos) throws Exception {
        if (client == null) {
            connect();
        }
        for (int i = 0; i < 10; i++) {
            try {
                downdown(s, bos);
                break;
            } catch (java.net.SocketException e) {
                try {
                    releaseAll();
                } catch (Exception ee) {
                }
                connect();

            } catch (Exception e) {
                try {
                    releaseAll();
                } catch (Exception ee) {
                }
                if (e.getMessage().toLowerCase().indexOf("__返回值为空") >= 0) {
                    connect();
                } else {
                    throw e;
                }
            }
        }

    }

    /**
     * 下载
     *
     * @param s
     * @param bos
     * @throws Exception
     */
    private void downdown(String s, OutputStream bos) throws java.net.SocketException, Exception {
        String header = getHead(s.getBytes().length, false);
        writeData(header);
        writeData(s);
        completeWrite();

        Map head = readHttpHead();
        String output = readerror(head);
        if (output == null || "".equals(output)) throw new Exception("__返回值为空");
        output = new String(Base64.decode(output));
        output = output.replaceAll("'_yf_'", "\n");

        Hashtable downout = new Hashtable();
        ServiceInvokerUtil.stringToHashtable(output, downout);
        if (downout.get("errorMessage") != null) {
            String err = (String) downout.get("errorMessage");
            throw new Exception(err);
        }

        int inLen = readContentLength(head);
        readFile(bos, inLen);
    }


    public String getHead(int length, boolean closesocket) {
        String path = "/servlet/SomeServlet";
        String header = "POST " + path + " HTTP/1.0\r\n";
        header = header + "Content-Length: " + length + "\r\n";
        header = header + "Content-Type: multipart/form-data\r\n";
        if (!closesocket) {
            header = header + "Connection: download\r\n";
        }
        header = header + "\r\n";
        return header;
    }

    public void writeData(String data) throws Exception {
        client.getOutputStream().write(data.getBytes());
    }

    public void completeWrite() throws Exception {
        client.getOutputStream().flush();
    }

    public void writeFile(InputStream is, int off, int len) throws Exception {

        byte[] b = new byte[len];
        int length = is.read(b);
        client.getOutputStream().write(b, 0, length);
    }


    public String readerror(Map head) {
        return (String) head.get("yfv6filesystem");
    }

    public int readContentLength(Map head) throws Exception {
        int dataLen = Integer.parseInt((String) head.get("Content-length".toLowerCase()));
        return dataLen;
    }

    public String readResponse(int dataLen) throws Exception {
        InputStream is = client.getInputStream();
        byte[] buf = new byte[dataLen];

        int p = 0;
        int r = -1;
        while (p < dataLen) {
            r = is.read(buf, p, dataLen - p);
            if (r < 0) {
                break;
            }
            p = p + r;
        }
        return new String(buf);
    }

    public void readFile(OutputStream os, int dataLen) throws Exception {
        readFile(this.client, dataLen, os);
    }

    public static void readFile(Socket socket, int inLen, OutputStream os)
            throws IOException {
        try {
            InputStream in = socket.getInputStream();
            byte[] b = new byte[8096];
            int length = -1;
            int tmplen = 0;
            while (inLen > tmplen) {
                length = in.read(b);
                if (length == -1) break;
                os.write(b, 0, length);
                tmplen += length;
            }
        } catch (Exception ex) {
            log.error(ex, ex);
            return;
        }
    }


    public void releaseAll() {
        try {
            client.close();
        } catch (Exception e) {
        }
        client = null;
    }

    private static void splitLineTo(String line, Map map) {
        int index = line.indexOf(": ");
        if (index > 0) {
            String key = line.substring(0, index);
            String value = line.substring(index + ": ".length(), line.length());
            map.put(key.toLowerCase(), value);
        }
    }

    public Map readHttpHead() throws IOException {
        InputStream is = this.client.getInputStream();
        Map map = new HashMap();

        String input = null;
        String line = null;
        String firstLine = null;
        int dataLen = 0;
        int len = 0;
        boolean html = false;

        while (true) {
            int b = 0;
            int b1 = 0;
            ByteArrayOutputStream bf = new ByteArrayOutputStream();
            for (; ; ) {
                b1 = b;
                b = is.read();
                if (b == -1) {
                    break;
                }
                if ((b != 13) && (b != 10)) {
                    bf.write(b);
                }
                if ((b == 10)) {
                    break;
                }
            }
            line = bf.toString();
            if (firstLine == null) {
                firstLine = new String(line);
            }

            if (line.regionMatches(true, 0, "Content-length: ", 0, 16)) {
                String t = line.substring(16, line.length());
                if (!t.equals("")) {
                    dataLen = Integer.valueOf(t).intValue();
                } else {
                    dataLen = 0;
                }
            }

            splitLineTo(line, map);
            len = line.length();

            if (len == 0) {
                break;
            }
        }

        return map;
    }

}
