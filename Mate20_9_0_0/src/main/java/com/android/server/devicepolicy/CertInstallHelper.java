package com.android.server.devicepolicy;

import android.util.Log;
import com.android.org.bouncycastle.asn1.ASN1InputStream;
import com.android.org.bouncycastle.asn1.ASN1Sequence;
import com.android.org.bouncycastle.asn1.DEROctetString;
import com.android.org.bouncycastle.asn1.x509.BasicConstraints;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CertInstallHelper {
    private static final String TAG = "DPMS_CerInstallHelper";

    private static boolean isCa(X509Certificate cert) {
        ASN1InputStream asn1InputString = null;
        try {
            byte[] asn1EncodedBytes = cert.getExtensionValue("2.5.29.19");
            if (asn1EncodedBytes == null) {
                return false;
            }
            DEROctetString derOctetString = (DEROctetString) new ASN1InputStream(asn1EncodedBytes).readObject();
            if (derOctetString == null) {
                return false;
            }
            return BasicConstraints.getInstance((ASN1Sequence) new ASN1InputStream(derOctetString.getOctets()).readObject()).isCA();
        } catch (IOException e) {
            if (asn1InputString != null) {
                try {
                    asn1InputString.close();
                } catch (IOException e2) {
                    Log.d(TAG, "IOException when close asn1InputString");
                }
            }
            return false;
        }
    }

    public static boolean installPkcs12Cert(String password, byte[] certBuffer, String certAlias, int certInstallType) throws Exception {
        Log.d(TAG, "#extracted pkcs12 certs from cert buffer");
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(new ByteArrayInputStream(certBuffer), password.toCharArray());
        Enumeration<String> aliases = keystore.aliases();
        if (!aliases.hasMoreElements()) {
            return false;
        }
        PasswordProtection pwd = new PasswordProtection(password.toCharArray());
        while (aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            if (keystore.isKeyEntry(alias)) {
                Entry entry = keystore.getEntry(alias, pwd);
                if (entry instanceof PrivateKeyEntry) {
                    return installCertToKeyStore((PrivateKeyEntry) entry, certAlias, certInstallType);
                }
            } else {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Skip non-key entry, alias = ");
                stringBuilder.append(alias);
                Log.d(str, stringBuilder.toString());
            }
        }
        return true;
    }

    private static boolean installCertToKeyStore(PrivateKeyEntry entry, String alias, int certInstallType) {
        PrivateKey userKey = entry.getPrivateKey();
        X509Certificate userCert = (X509Certificate) entry.getCertificate();
        Certificate[] certs = entry.getCertificateChain();
        List<X509Certificate> caCerts = new ArrayList(certs.length);
        for (Certificate c : certs) {
            X509Certificate cert = (X509Certificate) c;
            if (isCa(cert)) {
                caCerts.add(cert);
            }
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("# ca certs extracted = ");
        stringBuilder.append(caCerts.size());
        Log.d(str, stringBuilder.toString());
        return CertInstaller.installCert(alias == null ? "" : alias, userKey, userCert, caCerts, certInstallType);
    }

    public static boolean installX509Cert(byte[] bytes, String alias, int certInstallType) {
        if (bytes == null) {
            return false;
        }
        X509Certificate userCert = null;
        List<X509Certificate> caCerts = new ArrayList();
        try {
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bytes));
            if (isCa(cert)) {
                Log.d(TAG, "got a CA cert");
                caCerts.add(cert);
            } else {
                Log.d(TAG, "got a user cert");
                userCert = cert;
            }
        } catch (CertificateException e) {
            Log.w(TAG, "install X509 Cert error");
        }
        return CertInstaller.installCert(alias == null ? "" : alias, null, userCert, caCerts, certInstallType);
    }
}
