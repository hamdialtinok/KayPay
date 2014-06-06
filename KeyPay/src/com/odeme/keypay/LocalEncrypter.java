package com.odeme.keypay;

import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;

public class LocalEncrypter {
	
	private static String algorithm;
    private static Key key = null;
    private static Cipher cipher = null;
    private static String code=null;
    private static int bitShift=2;

    private static void setUp() throws Exception {
        key = KeyGenerator.getInstance(algorithm).generateKey();
        cipher = Cipher.getInstance(algorithm);
    }
    
	protected LocalEncrypter(String algo,int bit) throws Exception {
		super();
		// TODO Auto-generated constructor stub
		algorithm=algo;
		bitShift=bit;
		setUp();
	}
	
	protected String GetCrypted(String str) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException
	{
		code=asHex(dencrypt(encrypt(str)) );
		return code;
	}
    

    private static byte[] encrypt(String input)
        throws InvalidKeyException, 
               BadPaddingException,
               IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] inputBytes = input.getBytes();
        return cipher.doFinal(inputBytes);
    }
    private static byte[] dencrypt(byte[] input)
            throws InvalidKeyException, 
                   BadPaddingException,
                   IllegalBlockSizeException {
    	cipher.init(Cipher.DECRYPT_MODE, key);
	       byte[] original =
	         cipher.doFinal(input);
	       return original;
        }

    /*private static String decrypt(byte[] encryptionBytes)
        throws InvalidKeyException, 
               BadPaddingException,
               IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] recoveredBytes = 
          cipher.doFinal(encryptionBytes);
        System.out.println("Decrypted:"+asHex(recoveredBytes));
        String recovered = 
          new String(recoveredBytes);
        return recovered;
      }*/
    private static String asHex (byte buf[]) {
	      StringBuffer strbuf = new StringBuffer(buf.length * 2);
	      int i;

	      for (i = 0; i < buf.length; i++) {
	       if (((int) buf[i] & 0xff) < 0x10)
		    strbuf.append("0");

	       strbuf.append(Long.toString((int) buf[i] & 0xff, bitShift));
	      }
	      return strbuf.toString();
	     }
}
