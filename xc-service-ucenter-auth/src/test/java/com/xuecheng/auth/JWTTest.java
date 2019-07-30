package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hotwater on 2018/7/15.
 *
 * 进行测试JWT的令牌生成
 */
public class JWTTest {


    /**
     * 测试生成令牌
     */
    @Test
    public void test(){
        String key_location = "xc.keystore";//证书库
        String keystore_password = "xuechengkeystore";//密钥库密码
        ClassPathResource resource = new ClassPathResource(key_location);
//密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,
                keystore_password.toCharArray());
//密钥的密码，此密码和别名要匹配
        String keypassword = "xuecheng";
//密钥别名
        String alias = "xckey";
//密钥对（密钥和公钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypassword.toCharArray());
//密钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
//定义payload信息
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        tokenMap.put("name", "mrt");
        tokenMap.put("roles", "r01,r02");
        tokenMap.put("ext", "1");
//生成jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));
//取出jwt令牌
        String token = jwt.getEncoded();
        System.out.println("token="+token);
        testverify(token);
    }

//    @Test
    public void testverify(String token){
//jwt令牌
          token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE1MzE5NDM1MzMsImp0aSI6IjYwZmYxYTg1LWI4MmQtNGJlMi05YThmLTc3YmM5NTIyMmJhYyIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.TC8-RVY-eJzVWRx-liqW6v7BHuPdUSDH6lAErw4pZl-kiqzdgF4KOu6ku8XKjZA8oeVgG-WUZZosI8mYR0VxNwEtNfZtSqVeB8a1hRxEljDTprHuzOB5KQY0U_uSkccq2c9hYE_E08ak_JanZVqofBK-DntzAKRZX3qzMVa_sGmZgxkuSX8OW9nG7Bx9Q0YPsQFNN4eQ4U50D_vxKQbu-2pPa_V44uFsUPhfl8N1r9k-jeDlRieYQfNAJe3WXgG0P6iAwKLVchT45dSBmLMlYOSvRtgrhigNa3b9dKI8qcmWQRNGIn0TxTBjc-TShlgVjdNXuRx5LumqHG42I08U8w";
//公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgWlthSBeupjCp5s3aVk95utEGr8y9p02GGyBmWVK1oNC+IQo4HxtdgIAryDggxdlOPjlTVG8l/vLEJMx6mBk1YkDUDYsshe88sQPDcR4H7muSLNC4pzc+3O02GpuCxKQtuUYqr1vdHzGONFzpE33qmFuy29IhY7ncG9fSUrrc26bYQtDsoDvQBam7egJ8qbrf5PWt4pZUgYizHy3eTm97Qd18HlEHjqRyFxy2eTbFLyVToW6XveXBSi6vWtkbs59KNrWJtfRR8hZfexknufy9i2rCkMsHyAad+TSdmZlRfADBJtzl+mR7cqAIqqFuAySJLT70xrRna0IXlUjhHgXvQIDAQAB-----END PUBLIC KEY-----";

//jwt对象
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));
//获取jwt原始内容
        String claims = jwt.getClaims();
//jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }




}
