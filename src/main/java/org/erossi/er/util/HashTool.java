package org.erossi.er.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public enum HashTool {
    MD5("MD5"),
    SHA1("SHA1"),
    SHA256("SHA-256"),
    SHA512("SHA-512");

    private String name;

    HashTool() {
      this.name = "SHA-256";
    }

    HashTool(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public byte[] checksum(File input) throws FileNotFoundException, NoSuchAlgorithmException, IOException {
      InputStream ins = new FileInputStream(input);        
      try {
        MessageDigest digest = MessageDigest.getInstance(getName());
        byte[] block = new byte[4096];
        int length;
        while ((length = ins.read(block)) > 0) {
          digest.update(block, 0, length);
        }
        return digest.digest();            
      } finally {
        ins.close();
      }
    }

    public String checksumBase64(File input) throws FileNotFoundException, NoSuchAlgorithmException, IOException {
      byte[] response = this.checksum(input);
      return Base64.encodeBase64String(response);
    }
}