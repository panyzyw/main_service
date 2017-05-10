package com.yongyida.robot.voice.frame.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2016/4/7 0007.
 */
public class Http {
    // 文本协议Content-type
    public static final String text = "text/plain;charset=UTF-8";
    // 标准的POST协议Content-type
    public static final String post = "application/x-www-form-urlencoded;charset=UTF-8";

    public enum ContentType {
        asf("video/x-ms-asf"), avi("video/avi"), mpg("ivideo/mpeg"), gif(
                "image/gif"), jpg("image/jpeg"), bmp("image/bmp"), png(
                "image/png"), wav("audio/wav"), mp3("audio/mpeg3"), html(
                "text/html"), txt("text/plain"), zip("application/zip"), doc(
                "application/msword"), xls("application/vnd.ms-excel"), rtf(
                "application/rtf"), all("application/octet-stream");

        private String type;

        private ContentType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String toString() {
            return type;
        }
    }

    protected String httpURL;
    protected URL url;
    // 请求头参�?
    protected Map<String, String> requestProperty = new HashMap<String, String>();
    // 连接超时时间
    protected int connectTimeout = 5000;
    // 响应超时时间
    protected int readTimeout = 5000;
    // 编码
    protected String charset = "iso8859-1";

    /**
     * @param httpURL
     *            URL地址
     * @throws Exception
     */
    public Http(String httpURL) throws Exception {
        this.httpURL = httpURL;
        init();
    }

    /**
     * 自定义POST请求
     *
     * @param params
     *            post请求参数
     * @return 响应
     * @throws Exception
     */
    public String post(String params) throws Exception {
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        if (httpURL.startsWith("https")) {
            HttpsURLConnection hucs = (HttpsURLConnection) huc;
            // 閿熸枻鎷烽敓鏂ゆ嫹SSLContext閿熸枻鎷烽敓瑗燂紝璇ф嫹浣块敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鎸囬敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻娇鐧告嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰閿熸枻鎷
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷稴SLContext閿熸枻鎷烽敓鏂ゆ嫹閿熷彨寰楃鎷稴SLSocketFactory閿熸枻鎷烽敓鏂ゆ�?
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            hucs.setSSLSocketFactory(ssf);
            HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
            hucs.setHostnameVerifier(new TrustAnyHostnameVerifier());
        }
        huc.setRequestMethod("POST");
        huc.setDoOutput(true);
        huc.setDoInput(true);
        huc.setConnectTimeout(connectTimeout);
        huc.setReadTimeout(readTimeout);
        for (String property : requestProperty.keySet()) {
            huc.setRequestProperty(property, requestProperty.get(property));
        }
        huc.connect();

        OutputStream out = huc.getOutputStream();
        out.write(params.toString().getBytes(charset));
        out.flush();
        out.close();

        String err = checkError(huc);
        if (err != null&&!err.equals("200"))
            return err;

        BufferedReader in = new BufferedReader(new InputStreamReader(
                huc.getInputStream(), charset));
        StringBuffer resp = new StringBuffer();

        String s = in.readLine();
        while (s != null) {
            resp.append(s);
            s = in.readLine();
        }
        in.close();

        return resp.toString();
    }

    /**
     * 标准POST请求
     *
     * @param params
     *            post请求参数
     * @return 响应
     * @throws Exception
     */
    public String post(Map<String, String> params) throws Exception {
        StringBuffer s = new StringBuffer();
        // k=v&k=v
        for (String k : params.keySet()) {
            s.append("&").append(k).append("=").append(params.get(k));
        }
        s.deleteCharAt(0);
        return post(s.toString());
    }

    /**
     * 閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋�?
     *
     * @param fileParamName
     *            閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷风�?鐗熼敓鏂ゆ嫹閿熸枻鎷烽敓�?
     * @param fileName
     *            閿熶茎纭锋嫹閿熸枻鎷
     * @param file
     *            閿熶茎纭锋嫹
     * @return 閿熸枻鎷峰簲
     * @throws Exception
     */
    public String post(String fileParamName, String fileName, byte[] file)
            throws Exception {
        return post(fileParamName, fileName, file, null);
    }

    /**
     * 閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋�?
     *
     * @param fileParamName
     *            閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷风�?鐗熼敓鏂ゆ嫹閿熸枻鎷烽敓�?
     * @param fileName
     *            閿熶茎纭锋嫹閿熸枻鎷
     * @param file
     *            閿熶茎纭锋嫹
     * @param params
     *            閿熸枻鎷烽敓鏂ゆ�?
     * @return 閿熸枻鎷峰簲
     * @throws Exception
     */
    public String post(String fileParamName, String fileName, byte[] file,
                       Map<String, String> params) throws Exception {
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        if (httpURL.startsWith("https")) {
            HttpsURLConnection hucs = (HttpsURLConnection) huc;
            // 閿熸枻鎷烽敓鏂ゆ嫹SSLContext閿熸枻鎷烽敓瑗燂紝璇ф嫹浣块敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鎸囬敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻娇鐧告嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰閿熸枻鎷
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷稴SLContext閿熸枻鎷烽敓鏂ゆ嫹閿熷彨寰楃鎷稴SLSocketFactory閿熸枻鎷烽敓鏂ゆ�?
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            hucs.setSSLSocketFactory(ssf);
            HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
            hucs.setHostnameVerifier(new TrustAnyHostnameVerifier());
        }
        huc.setRequestMethod("POST");
        huc.setDoOutput(true);
        huc.setDoInput(true);
        huc.setConnectTimeout(connectTimeout);
        huc.setReadTimeout(readTimeout);

        // 閿熻闈╂嫹
        String boundary = "-----------------------------114975832116442893661388290519";
        huc.setRequestProperty("Content-Type", "multipart/form-data;boundary="
                + boundary);

        boundary = "--" + boundary;

        StringBuffer sb = new StringBuffer();
        // 閿熸枻鎷烽敓鏂ゆ�?
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String k = it.next();
                String v = params.get(k);
                sb.append(boundary).append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + k
                        + "\"\r\n\r\n");
                sb.append(v).append("\r\n");
            }
        }

        // 閿熶茎纭锋嫹
        sb.append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"" + fileParamName
                + "\"; filename=\"" + fileName + "\"\r\n");
        sb.append("Content-Type: " + getContentType(fileName) + " \r\n\r\n");

        huc.connect();

        OutputStream out = huc.getOutputStream();
        out.write(sb.toString().getBytes(charset));
        out.write(file);
        out.flush();
        out.close();

        String err = checkError(huc);
        if (err != null&&!err.equals("200"))
            return err;

        BufferedReader in = new BufferedReader(new InputStreamReader(
                huc.getInputStream(), charset));
        StringBuffer resp = new StringBuffer();

        String s = in.readLine();
        while (s != null) {
            resp.append(s);
            s = in.readLine();
        }
        in.close();

        return resp.toString();
    }

    /**
     * 閿熸枻鎷峰簲閿熶茎纭锋嫹
     *
     * @param resp
     * @param fileName
     *            閿熶茎纭锋嫹閿熸枻鎷
     * @param file
     *            閿熶茎纭锋嫹
     * @return boolean 閿熻鍑ゆ嫹閿熸枻鎷峰簲閿熺即鐧告嫹
     * @throws Exception
     */
    public static boolean response(HttpServletResponse resp, String fileName,
                                   byte[] file) {
        if (resp == null || fileName == null || fileName.trim().equals("")
                || file == null || file.length == 0) {
            throw new NullPointerException("param is null");
        }
        try {// 閿熶茎纭锋嫹閿熸枻鎷烽敓鏂ゆ嫹閿熶茎鎲嬫嫹閿熸枻�?
            fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        resp.reset();
        resp.setCharacterEncoding("UTF-8");
        resp.addHeader("Content-Disposition", "attachment;filename=" + fileName
                + ";");
        resp.setContentType(getContentType(fileName));
        try {
            OutputStream out = resp.getOutputStream();
            out.write(file);
            out.flush();
            out.close();
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    /**
     * GET请求
     *
     * @return String 閿熸枻鎷峰簲
     * @throws Exception
     */
    public String get(Map<String, String> params)
            throws Exception {
        StringBuffer s = new StringBuffer();
        // k=v&k=v
        for (String k : params.keySet()) {
            s.append("&").append(k).append("=").append(params.get(k));
        }
        s.deleteCharAt(0);
        httpURL = httpURL + "?" + s.toString();
        return get(httpURL);
    }

    /**
     * GET请求
     * @return
     * @throws Exception
     */
    public String get() throws Exception {
        return get(httpURL);
    }

    /**
     * GET请求
     *
     * @return String 閿熸枻鎷峰簲
     * @throws Exception
     */
    public String get(String httpURL) throws Exception {
        URL url = new URL(httpURL);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        if (httpURL.startsWith("https")) {
            HttpsURLConnection hucs = (HttpsURLConnection) huc;
            // 閿熸枻鎷烽敓鏂ゆ嫹SSLContext閿熸枻鎷烽敓瑗燂紝璇ф嫹浣块敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鎸囬敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻娇鐧告嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰閿熸枻鎷
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷稴SLContext閿熸枻鎷烽敓鏂ゆ嫹閿熷彨寰楃鎷稴SLSocketFactory閿熸枻鎷烽敓鏂ゆ�?
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            hucs.setSSLSocketFactory(ssf);
            HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
            hucs.setHostnameVerifier(new TrustAnyHostnameVerifier());
        }
        huc.setRequestMethod("GET");
        huc.setDoOutput(false);
        huc.setDoInput(true);
        huc.setConnectTimeout(5000);
        huc.setReadTimeout(5000);
        huc.connect();

        String err = checkError(huc);
        if (err != null)
            return err;

        BufferedReader in = new BufferedReader(new InputStreamReader(
                huc.getInputStream(), charset));
        StringBuffer resp = new StringBuffer();

        String s = in.readLine();
        while (s != null) {
            resp.append(s);
            s = in.readLine();
        }
        in.close();

        return resp.toString();
    }

    public ByteArrayInputStream getByteInputStream() throws Exception {
        URL url = new URL(httpURL);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        if (httpURL.startsWith("https")) {
            HttpsURLConnection hucs = (HttpsURLConnection) huc;
            // 閿熸枻鎷烽敓鏂ゆ嫹SSLContext閿熸枻鎷烽敓瑗燂紝璇ф嫹浣块敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鎸囬敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻娇鐧告嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰閿熸枻鎷
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷稴SLContext閿熸枻鎷烽敓鏂ゆ嫹閿熷彨寰楃鎷稴SLSocketFactory閿熸枻鎷烽敓鏂ゆ�?
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            hucs.setSSLSocketFactory(ssf);
            HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
            hucs.setHostnameVerifier(new TrustAnyHostnameVerifier());
        }
        huc.setRequestMethod("GET");
        huc.setDoOutput(false);
        huc.setDoInput(true);
        huc.setConnectTimeout(5000);
        huc.setReadTimeout(5000);
        huc.setRequestProperty("Content-type", "text/plain;charset=" + charset);
        huc.connect();

        String err = checkError(huc);
        if (err != null)
            return null;

        int countent_length = huc.getContentLength();
        byte[] datas = new byte[countent_length];

        InputStream is = huc.getInputStream();
        is.read(datas);

        ByteArrayInputStream bis = new ByteArrayInputStream(datas, 0,countent_length);
        return bis;
    }


    /**
     * 閿熸枻鎷峰彇閿熶茎纭锋嫹閿熸枻鎷烽敓鏂ゆ�?
     *
     * @param fileName
     * @return String
     */
    public static String getContentType(String fileName) {
        // 閿熶茎纭锋嫹閿熸枻鎷峰皬�?
        String filename = fileName.toLowerCase();
        if (filename.endsWith(".asf")) {
            return ContentType.asf.toString();
        } else if (filename.endsWith(".avi")) {
            return ContentType.avi.toString();
        } else if (filename.endsWith(".mpg") || filename.endsWith(".mpeg")) {
            return ContentType.mpg.toString();
        } else if (filename.endsWith(".gif")) {
            return ContentType.gif.toString();
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return ContentType.jpg.toString();
        } else if (filename.endsWith(".bmp")) {
            return ContentType.bmp.toString();
        } else if (filename.endsWith(".png")) {
            return ContentType.png.toString();
        } else if (filename.endsWith(".wav")) {
            return ContentType.wav.toString();
        } else if (filename.endsWith(".mp3")) {
            return ContentType.mp3.toString();
        } else if (filename.endsWith(".htm") || filename.endsWith(".html")) {
            return ContentType.html.toString();
        } else if (filename.endsWith(".txt")) {
            return ContentType.txt.toString();
        } else if (filename.endsWith(".zip")) {
            return ContentType.zip.toString();
        } else if (filename.endsWith(".doc")) {
            return ContentType.doc.toString();
        } else if (filename.endsWith(".xls")) {
            return ContentType.xls.toString();
        } else if (filename.endsWith(".rtf")) {
            return ContentType.rtf.toString();
        }

        return ContentType.all.toString();
    }

    /**
     * 閿熸枻鎷烽敓鏂ゆ嫹鏂愶拷
     *
     * @param huc
     *            閿熸枻鎷烽敓鏂ゆ�?
     * @return String 閿熷眾甯搁敓鏂ゆ嫹搴
     * @throws Exception
     *             String
     */
    private static String checkError(HttpURLConnection huc) throws Exception {

        return huc.getResponseCode()+"";
    }

    //
    private void init() throws Exception {
        if (httpURL == null
                || (!httpURL.startsWith("http://") && !httpURL
                .startsWith("https://"))) {
            throw new NullPointerException("param is't url-" + httpURL);
        }
        url = new URL(httpURL);
        requestProperty.put("Content-type", text);
        // requestProperty.put("Content-type", post);
    }

    public static class MyX509TrustManager implements
            javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        X509TrustManager sunJSSEX509TrustManager;

        MyX509TrustManager() throws Exception {
            // create a "default" JSSE X509TrustManager.

            System.setProperty("javax.net.ssl.trustStore", "*.keystore");
            System.setProperty("java.protocol.handler.pkgs",
                    "com.sun.net.ssl.internal.www.protocol");
            //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");

            KeyStore ks = KeyStore.getInstance("JKS");
            // ks.load(new
            // FileInputStream("trustedCerts"),"passphrase".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    "SunX509", "SunJSSE");
            tmf.init(ks);
            TrustManager tms[] = tmf.getTrustManagers();
			/*
			 * Iterate over the returned trustmanagers, look for an instance of
			 * X509TrustManager. If found, use that as our "default" trust
			 * manager.
			 */
            for (int i = 0; i < tms.length; i++) {
                if (tms[i] instanceof X509TrustManager) {
                    sunJSSEX509TrustManager = (X509TrustManager) tms[i];
                    return;
                }
            }
			/*
			 * Find some other way to initialize, or else we have to fail the
			 * constructor.
			 */
            throw new Exception("Couldn't initialize");
        }

        /*
         * Delegate to the default trust manager.
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            try {
                sunJSSEX509TrustManager.checkClientTrusted(chain, authType);
            } catch (CertificateException excep) {
                // do any special handling here, or rethrow exception.
            }
        }

        /*
         * Delegate to the default trust manager.
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            try {
                sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException excep) {
				/*
				 * Possibly pop up a dialog box asking whether to trust the cert
				 * chain.
				 */
            }
        }

        /*
         * Merely pass this through.
         */
        public X509Certificate[] getAcceptedIssuers() {
            return sunJSSEX509TrustManager.getAcceptedIssuers();
        }
    }

    public static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }

    }

    public String getHttpURL() {
        return httpURL;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 设置连接超时时间
     *
     * @param connectTimeout
     *            毫秒
     */
    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout < 1)
            return;
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * 设置响应超时时间
     *
     * @param readTimeout
     *            毫秒
     */
    public void setReadTimeout(int readTimeout) {
        if (readTimeout < 1)
            return;
        this.readTimeout = readTimeout;
    }

    /**
     * 设置请求头参�?
     *
     * @param
     */
    public void setRequestProperty(String key, String value) {
        requestProperty.put(key, value);
    }

    /**
     * 设置编码
     * @param charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }
}
