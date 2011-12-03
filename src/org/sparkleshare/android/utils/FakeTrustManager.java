package org.sparkleshare.android.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class FakeTrustManager implements X509TrustManager {

    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public boolean isClientTrusted(X509Certificate[] chain) {
            return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain) {
            return true;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
            return _AcceptedIssuers;
    }

}