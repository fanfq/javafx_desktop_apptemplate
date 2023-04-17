package com.elseplus.createconfig.test;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

public class RSATest {

    @Test
    public void test(){

        try{
            KeyStore ks = KeyStore.getInstance("pkcs12");
            FileInputStream in = new FileInputStream("../RSA_PKCS12.keystore");
            ks.load(in, "elseplus.com".toCharArray());
            PrivateKey key = (PrivateKey) ks.getKey("elseplus.com", "elseplus.com".toCharArray());
            System.out.println("key: " + key);

            //PublicKey publicKey = (PublicKey) ks.getKey("elseplus.com", "elseplus.com".toCharArray());
            //System.out.println("publicKey: " + publicKey);

        }catch (Exception e){
            e.printStackTrace();
        }





//            Configuration config = Configuration.builder()
//                    .baseUri(BASE_URL + "/app")
//                    .basePath("${user.dir}/app")
//                    .files(refs)
//                    .property("maven.central", MAVEN_BASE)
//                    .signer(key)
//                    .build();

            //签名相关
            //https://github.com/update4j/update4j/issues/5
//            try (Writer out = Files.newBufferedWriter(Paths.get(dir + "/config.xml"))) {
//                config.write(out);
//            }
//        }
    }
}
