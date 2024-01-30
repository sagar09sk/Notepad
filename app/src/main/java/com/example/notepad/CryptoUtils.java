package com.example.notepad;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoUtils {

    private static final String KEY_ALIAS = "YourKeyAlias";
    private static final String TRANSFORMATION = "AES/CBC/PKCS7Padding";

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] iv = cipher.getIV();

        // Concatenate IV and encrypted data
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        String base64Encoded = Base64.encodeToString(combined, Base64.DEFAULT);

        return base64Encoded.replace('/', '_');
    }

    public static String decrypt(String encryptedData) throws Exception {

        String base64Encoded = encryptedData.replace('_', '/');
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // Decode Base64
        byte[] combined = Base64.decode(base64Encoded, Base64.DEFAULT);

        // Extract IV
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // Extract encrypted data
        byte[] encryptedBytes = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);

        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new IvParameterSpec(iv));

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


    private static boolean keyExists() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore.containsAlias(KEY_ALIAS);
        } catch (Exception e) {
            return false;
        }
    }

    private static SecretKey getSecretKey(){
        SecretKey secretKey = null;
        try {
            if (!keyExists()) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                keyGenerator.init(new KeyGenParameterSpec.Builder("YourKeyAlias", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setKeySize(128) // Set the desired key size
                        .build());
                secretKey = keyGenerator.generateKey();

            }
            else {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                Key key = keyStore.getKey(KEY_ALIAS, null);
                if (key instanceof SecretKey) {
                    return (SecretKey) key;

                } else {
                    throw new Exception("Invalid key type");
                }
            }


        } catch (Exception e) {
            Log.d("Exception KeyStoreHelper" , "Exception -> " +e);
        }

        return secretKey;
    }

}