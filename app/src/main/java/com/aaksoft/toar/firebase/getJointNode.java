package com.aaksoft.toar.firebase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// utility function written to find the unique node of two users


public class getJointNode {


    public static String getUniqueNode(String uid1, String uid2) throws NoSuchAlgorithmException{
        String concatenatedIds;
        int compare = uid1.compareTo(uid2);
        if (compare < 0){
            concatenatedIds = uid1+uid2;


        }
        else if (compare > 0) {
            // System.out.println(uid2+" is before "+Person1);
            concatenatedIds = uid2+uid1;

        }
        else {
            return "MismatchedIds";

        }

        return sha256(concatenatedIds);
    }

    // takes a string input and return sha 256 as string
    public static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}