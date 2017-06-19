package com.zccl.ruiqianqi.tools.http;

import android.content.Context;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by ruiqianqi on 2017/6/8 0008.
 */

public class Crt {

    private static String TAG = Crt.class.getSimpleName();

    /**
     * 方案1
     * 不论是权威机构颁发的证书还是自签名的，打包一份到 app 内部，比如存放在 asset 里。
     * 通过这份内置的证书初始化一个KeyStore，然后用这个KeyStore去引导生成的TrustManager来提供验证，
     * 具体代码如下：
     *
     * @param context
     * @return
     */
    public static SSLContext getSSLContext(Context context, String fileName) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // uwca.crt 打包在 asset 中，该证书可以从https://itconnect.uw.edu/security/securing-computer/install/safari-os-x/下载
            InputStream caInput = new BufferedInputStream(context.getAssets().open(fileName));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                LogUtils.e(TAG, "ca = " + ((X509Certificate) ca).getSubjectDN());
                LogUtils.e(TAG, "key = " + ca.getPublicKey());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1","AndroidOpenSSL");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext;

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 方案2
     * 同方案1，打包一份到证书到 app 内部，但不通过KeyStore去引导生成的TrustManager，
     * 而是干脆直接自定义一个TrustManager，自己实现校验逻辑；校验逻辑主要包括：
     *
     * 服务器证书是否过期
     * 证书签名是否合法
     *
     * @param context
     * @param fileName
     * @return
     */
    public static SSLContext getSSLContext2(Context context, String fileName) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // uwca.crt 打包在 asset 中，该证书可以从https://itconnect.uw.edu/security/securing-computer/install/safari-os-x/下载
            InputStream caInput = new BufferedInputStream(context.getAssets().open(fileName));
            final Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                LogUtils.e(TAG, "ca = " + ((X509Certificate) ca).getSubjectDN());
                LogUtils.e(TAG, "key = " + ca.getPublicKey());
            } finally {
                caInput.close();
            }

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLSv1", "AndroidOpenSSL");
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            for (X509Certificate cert : chain) {

                                // Make sure that it hasn't expired.
                                cert.checkValidity();

                                // Verify the certificate's public key chain.
                                try {
                                    cert.verify(ca.getPublicKey());
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeyException e) {
                                    e.printStackTrace();
                                } catch (NoSuchProviderException e) {
                                    e.printStackTrace();
                                } catch (SignatureException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, null);

            return sslContext;

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }
}
