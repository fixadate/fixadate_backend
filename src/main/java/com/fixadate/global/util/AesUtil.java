package com.fixadate.global.util;

import static com.fixadate.global.util.constant.ConstantValue.*;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.fixadate.global.util.converter.Key;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AesUtil {
	private static String privateKey_256 = Key.AES_KEY;
	private static String private_iv = Key.VECTOR;

	public static String aesCBCEncode(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException,
		InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException,
		BadPaddingException {
		SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(StandardCharsets.UTF_8),
			AES_ALGORITHM.getValue());
		IvParameterSpec iv = new IvParameterSpec(private_iv.getBytes());
		Cipher c = Cipher.getInstance(CIPHER_INSTANCE.getValue());
		c.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		byte[] encryptionByte = c.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

		return Hex.encodeHexString(encryptionByte);
	}

	public static String aesCBCDecode(String encodedText) throws NoSuchPaddingException, NoSuchAlgorithmException,
		InvalidAlgorithmParameterException, InvalidKeyException, DecoderException,
		IllegalBlockSizeException, BadPaddingException {

		SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(StandardCharsets.UTF_8),
			AES_ALGORITHM.getValue());
		IvParameterSpec iv = new IvParameterSpec(private_iv.getBytes());
		Cipher c = Cipher.getInstance(CIPHER_INSTANCE.getValue());
		c.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] decodedByte = Hex.decodeHex(encodedText.toCharArray());

		return new String(c.doFinal(decodedByte), StandardCharsets.UTF_8);
	}

}
