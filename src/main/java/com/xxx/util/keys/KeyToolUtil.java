package com.xxx.util.keys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

import sun.misc.BASE64Encoder;

public class KeyToolUtil {

	private File keystoreFile;
	private String keyStoreType;
	private char[] password;
	private String alias;
	private File exportedPrivateKey;
	private File exportedPublicKey;

	public void genKayPair() throws Exception{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
	      keyGen.initialize(1024, new SecureRandom());
	      KeyPair pair = keyGen.generateKeyPair();
	      PrivateKey priv = pair.getPrivate();
	      PublicKey pub = pair.getPublic();
	      FileOutputStream ostream = new FileOutputStream(exportedPrivateKey + ".private");
	      System.out.println("**************************************************");
	      System.out.println(exportedPrivateKey);
	      System.out.println("**************************************************");
	      
	      ObjectOutputStream p = new ObjectOutputStream(ostream);
	      p.writeObject(priv);
	      p.close();
	      ostream = new FileOutputStream(exportedPublicKey + ".public");
	      p = new ObjectOutputStream(ostream);
	      p.writeObject(pub);
	      p.close();
	}
	public static KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
		try {
			Key key = keystore.getKey(alias, password);
			if (key instanceof PrivateKey) {
				Certificate cert = keystore.getCertificate(alias);
				PublicKey publicKey = cert.getPublicKey();
				return new KeyPair(publicKey, (PrivateKey) key);
			}
		} catch (UnrecoverableKeyException e) {
		} catch (NoSuchAlgorithmException e) {
		} catch (KeyStoreException e) {
		}
		return null;
	}

	public void exportPrivateKey() throws Exception {
		KeyStore keystore = KeyStore.getInstance(keyStoreType);
		BASE64Encoder encoder = new BASE64Encoder();
		keystore.load(new FileInputStream(keystoreFile), password);
		KeyPair keyPair = getPrivateKey(keystore, alias, password);
		PrivateKey privateKey = keyPair.getPrivate();
//		String encoded = encoder.encode(privateKey.getEncoded());
//		FileWriter fw = new FileWriter(exportedPrivateKey);
//		fw.write("—–BEGIN PRIVATE KEY—–\n");
//		fw.write(encoded);
//		fw.write("\n");
//		fw.write("—–END PRIVATE KEY—–");
//		fw.close();
		FileOutputStream fop = new FileOutputStream(exportedPrivateKey);
		fop.write(privateKey.getEncoded());
		fop.flush();
		fop.close();
	}

	public void exportPublicKey() throws Exception {
		KeyStore keystore = KeyStore.getInstance(keyStoreType);
		BASE64Encoder encoder = new BASE64Encoder();
		keystore.load(new FileInputStream(keystoreFile), password);
		KeyPair keyPair = getPrivateKey(keystore, alias, password);
		PublicKey publicKey = keyPair.getPublic();
//		String encoded = encoder.encode(publicKey.getEncoded());
//		FileWriter fw = new FileWriter(exportedPublicKey);
//		fw.write("—–BEGIN PUBLIC KEY—–\n");
//		fw.write(encoded);
//		fw.write("\n");
//		fw.write("—–END PUBLIC KEY—–");
//		fw.close();
		FileOutputStream fop = new FileOutputStream(exportedPublicKey);
		fop.write(publicKey.getEncoded());
		fop.flush();
		fop.close();
	}

	public static void main(String args[]) throws Exception {
		KeyToolUtil export = new KeyToolUtil();
//		export.keystoreFile = new File("D://xuyf/testKey.jks");
//		export.keyStoreType = "JKS";
//		export.password = "123456".toCharArray();
//		export.alias = "testKey";
//		export.exportedPrivateKey = new File("D://xuyf/testKey.private");
//		export.exportedPublicKey = new File("D://xuyf/testKey.public");
//		export.exportPrivateKey();l
//		export.exportPublicKey();
		export.exportedPrivateKey = new File("D://xuyf/testKey");
		export.exportedPublicKey = new File("D://xuyf/testKey");
		export.genKayPair();
	}
}
