package com.raihanul.votingbd;

import com.google.android.gms.common.util.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaUtils {
    public static String sha256(final String msg) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(msg.getBytes());
            byte[] out = digest.digest();
            return Hex.bytesToStringUppercase(out).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
