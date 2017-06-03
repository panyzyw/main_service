package com.zccl.ruiqianqi.tools.http;

import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import static com.zccl.ruiqianqi.tools.http.MySecurity.SECURITY.SSL;
import static com.zccl.ruiqianqi.tools.http.MySecurity.SECURITY.TLS;

/**
 * Created by ruiqianqi on 2017/3/14 0014.
 *
 * SSL(Secure Socket Layer)
 * 补充：IP（网络层）、TCP（传输层），SSL（加密层）、HTTP（应用层）
 *
 * 数字证书：一种文件的名称，好比一个机构或人的签名，能够证明这个机构或人的真实性。其中包含的信息，用于实现上述功能。
 * 加密和认证：加密是指通信双方为了防止敏感信息在信道上被第三方窃听而泄漏，将明文通过加密变成密文，如果第三方无法解密的话，
 * 就算他获得密文也无能为力；认证是指通信双方为了确认对方是值得信任的消息发送或接受方，而不是使用假身份的骗子，
 * 采取的确认身份的方式。只有同时进行了加密和认证才能保证通信的安全，因此在SSL通信协议中这两者都很重要。
 *
 * https依赖一种实现方式，目前通用的是SSL，
 * 数字证书是支持这种安全通信的文件。另外有SSL衍生出TLS和WTLS；
 * TLS是IEFT将SSL标准化之后产生的（TLS1.0、TLS1.1、TLS1.2），与SSL差别很小；
 * WTLS是用于无线环境下的TSL。
 *
 * Protocol	    Supported (API Levels)	Enabled by default (API Levels)
 * SSLv3	    1–TBD	                1–22
 * TLSv1.0	    1+	                    1+
 * TLSv1.1	    20+	                    20+
 * TLSv1.2	    20+	                    20+
 *
 * TLSv1.0从API 1+就被默认打开
 * TLSv1.1和TLSv1.2只有在API 20+ 才会被默认打开
 * 也就是说低于API 20+的版本是默认关闭对TLSv1.1和TLSv1.2的支持，若要支持则必须自己打开
 *
 * Name	Supported (API Levels)
 * Default	        10+
 * SSL	            10–TBD
 * SSLv3	        10–TBD
 * TLS	            1+
 * TLSv1.0	        10+
 * TLSv1.1	        16+
 * TLSv1.2	        16+
 *
 * For some reason, android supports TLS v1.2 from API 16, but enables it by
 * default only from API 20.
 *
 * JKS和JCEKS是Java密钥库(KeyStore)的两种比较常见类型，
 * JKS的Provider是SUN，在每个版本的JDK中都有。
 * BKS来自BouncyCastleProvider，它使用的也是TripleDES来保护密钥库中的Key，
 * 它能够防止证书库被不小心修改（Keystore的keyentry改掉1个bit都会产生错误），BKS能够跟JKS互操作。
 */

public class MySecurity {

    public enum SECURITY{

        SSL,

        // TLS是SSL的标准化后的产物
        // 有1.0 1.1 1.2三个版本
        // 默认使用1.0
        // TLS1.0和SSL3.0几乎没有区别
        TLS,
    }

    // 安全协议类型
    private SECURITY mSecurity;
    // 密钥管理器
    private KeyManager[] mKeyManagers;
    // 证书管理器
    private TrustManager[] mTrustManagers;

    public MySecurity(SECURITY security){
        mSecurity = security;
        try{
            getTrustManager(security);
        }catch (Exception e){

        }
    }

    /**
     * 安全传输协议上下文
     * @return
     */
    public SSLContext getSSLContext() {
        SSLContext sslContext = null;
        try {
            if (SSL == mSecurity) {
                sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            } else if (TLS == mSecurity) {
                sslContext = SSLContext.getInstance("TLSv1.2");
            }

            if(null != sslContext){
                sslContext.init(mKeyManagers, mTrustManagers, new java.security.SecureRandom());
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    /**
     * 证书管理器
     *
     * 要生成bks证书，需要bcprov-ext-jdk15on-151.jar（下载地址: http://www.bouncycastle.org/latest_releases.html）。
     * 且将该文件放到Java\jdk1.8.0_20\jre\lib\ext目录下。
     * 我们的后端同事提供了自签名的服务器证书server.crt，我们需要把这个server.crt转换成Android系统的bks格式证书。
     * 使用以下命令行：
     * keytool -importcert -trustcacerts -keystore e:\key.bks -file e:\server.crt -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider
     * 按照提示重复输入两次密码（在Java的KeyStore对象加载证书时会用到这个密码。），
     * 然后就成功将E:\目录下的server.crt转成key.bks证书。
     *
     * @param flag
     * @return
     */
    private void getTrustManager(SECURITY flag) {
        TrustManager[] trustManagers = null;
        try {
            if (SSL == flag) {
                trustManagers = new TrustManager[]{ new MyX509TrustManager() };
            } else if (TLS == flag) {

                /**
                 * 是client->server单向的SSL认证
                 */
                //KeyStore ks = KeyStore.getInstance("BKS");
                // client采用send.bks中的clientKey私钥进行数据加密，发送给server
                //ks.load(FileUtils.getFileStream(null, "send.bks", MyConfigure.SIX_ABSOLUTE), "clientPrivateKey".toCharArray());
                //KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                //kmf.init(ks, "clientPrivateKey".toCharArray());
                //mKeyManagers = kmf.getKeyManagers();

                /**
                 * 是server->client单向的SSL认证
                 */
                KeyStore tks = KeyStore.getInstance("BKS");
                // client采用recv.bks中的server.crt证书（包含了serverKey的公钥）对数据解密，如果解密成功，证明消息来自server，进行逻辑处理
                //tks.load(FileUtils.getFileStream(null, "recv.bks", MyConfigure.SIX_ABSOLUTE), "serverPublicKey".toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                //tmf.init(tks);
                tmf.init((KeyStore) null);
                trustManagers = tmf.getTrustManagers();

                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
            }
            mTrustManagers = trustManagers;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回证书管理器
     * @return
     */
    public X509TrustManager getTrustManager() {
        if(null != mTrustManagers && mTrustManagers.length > 0){
            return (X509TrustManager) mTrustManagers[0];
        }
        return null;
    }

    /**
     * 自定义证书管理器
     */
    public class MyX509TrustManager implements X509TrustManager {
        X509TrustManager sunJSSEX509TrustManager;

        MyX509TrustManager() throws Exception {
            // create a "default" JSSE X509TrustManager.

            // 1、若系统属性javax.net.sll.trustStore指定了TrustStore文件，那么信任管理器就去jre安装路径下的lib/security/目录中寻找并使用这个文件来检查证书。
            // 2、若该系统属性没有指定TrustStore文件，它就会去jre安装路径下寻找默认的TrustStore文件，这个文件的相对路径为：lib/security/jssecacerts。
            // 3、若jssecacerts不存在，但是cacerts存在（它随J2SDK一起发行，含有数量有限的可信任的基本证书），那么这个默认的TrustStore文件就是lib/security/cacerts。
            System.setProperty("javax.net.ssl.trustStore", "*.keystore");
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");

            /**************************************************************************************/
            /**
             * 是client->server单向的SSL认证
             */
            // 访问Java密钥库，JKS是keytool创建的Java密钥库
            //KeyStore ks = KeyStore.getInstance("JKS");
            // client采用send.jks中的clientkey私钥进行数据加密，发送给server
            //ks.load(FileUtils.getFileStream(null, "send.jks", MyConfigure.SIX_ABSOLUTE), "clientPrivateKey".toCharArray());

            //KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            //
            //kmf.init(ks, "clientPrivateKey".toCharArray());
            //mKeyManagers = kmf.getKeyManagers();

            /**************************************************************************************/
            /**
             * 是server->client单向的SSL认证
             */
            // 访问Java密钥库，JKS是keytool创建的Java密钥库
            KeyStore tks = KeyStore.getInstance("JKS");
            // client采用recv.jks中的server.crt证书（包含了serverkey的公钥）对数据解密，如果解密成功，证明消息来自server，进行逻辑处理
            //tks.load(FileUtils.getFileStream(null, "recv.jks", MyConfigure.SIX_ABSOLUTE), "serverPublicKey".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            // 保存服务端的授权证书
            tmf.init(tks);

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
            } catch (CertificateException e) {
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
            } catch (CertificateException e) {
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

    /**
     *
     */
    public static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
