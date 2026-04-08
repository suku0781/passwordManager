package com.shk.personalProject.passwordManager.vault.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AesEncryptionService {
    private static final String ALGORITHM = "AES/GCM/NoPadding"; // AES = 암호화 알고리즘 종류, GCM = 소금(IV)
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final SecretKeySpec secretKeySpec;

    // @Value 대신 생성자에서 한번만 키 생성.
    public AesEncryptionService(@Value("${app.encryption.key}") String encryptionKey) { // application.properties 에 적어둔 문자열을 가져옴. (이게 찐 마스터키)
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
        this.secretKeySpec = new SecretKeySpec(keyBytes, "AES"); // Base64로 풀어서 AES용 열쇠 객체로 만듦.
    }

    // 암호화
    public String encrypt(String plainText){
        try {
            // 매 호출 마다 새로운 IV(Initialization Vector, 같은 비밀번호를 암호화해도 다르게 나오게 하는 일회용 랜덤값) 생성.
            byte[] iv = new  byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // 매 호출 마다 새로운 Cipher 인스턴스 생성.
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv)); // 암호화 모드, 마스터키, 일회용 랜덤 값 세팅

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)); // 암호화 진행.

            /*
             * System.arraycopy()는 자바에서 배열의 특정 부분을 다른 배열로 아주 빠르게 복사할 때 사용하는 도구.
             * OS 레벨에서 메모리를 통째로 밀어 넣는 방식, 속도가 빠름.
             *
             * System.arraycopy(원본배열, 원본시작위치, 대상배열, 대상시작위치, 복사할길이);
             *
             * 복호화 할려면 암호화 할 때 쓴 일회용 랜덤값이 반드시 필요함.
             * 그래서 IV + 암호문 을 하나로 합쳐서 통째로 반환.
             * */

            // 1. IV(12)와 암호문(N)을 합친 크기의 빈 상자를 만든다.
            byte[] combined = new byte[iv.length + encrypted.length];
            // 2. IV를 combined의 0번 위치부터 복사해서 넣는다.
            System.arraycopy(iv, 0, combined, 0, iv.length);
            // 3. 암호문을 combined의 IV 바로 뒷 자리(iv.length)부터 복사해서 넣는다.
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    // 복호화
    public String decrypt(String encryptedText){
        try {
            // 1. Base64 디코딩 해서 바이트 덩어리를 가져온다.
            byte[] combined = Base64.getDecoder().decode(encryptedText);

            // 2. 나눌 준비. (빈 배열 생성.)
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];

            // 3.여기서 먼저 잘라서 IV와 암호문을 채우기.
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            // 4. 채워진 IV를 가지고 열쇠를 세팅한다.
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            // 5. 복호화 실행.
            return new String(cipher.doFinal(encrypted),  StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }
}
