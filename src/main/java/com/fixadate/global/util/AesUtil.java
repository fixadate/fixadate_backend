package com.fixadate.global.util;

import static com.fixadate.global.exception.ExceptionCode.*;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fixadate.global.exception.badrequest.EncryptionBadRequestException;
import com.fixadate.global.exception.notfound.EncryptionNotFoundException;

@Component
public final class AesUtil {
	private static String privateKey_256;
	private static String private_iv;

	@Value("${encryption.secret}")
	public void setPrivateKey_256(String key) {
		this.privateKey_256 = key;
	}

	@Value("${encryption.vector}")
	public void setPrivate_iv(String vector) {
		this.private_iv = vector;
	}

	private AesUtil() {

	}

	public static String aesCbcEncode(String plainText) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(StandardCharsets.UTF_8),
				AES_ALGORITHM.getValue());
			IvParameterSpec iv = new IvParameterSpec(private_iv.getBytes());
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE.getValue());
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] encryptionByte = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			return Hex.encodeHexString(encryptionByte);
		} catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
			throw new EncryptionNotFoundException(NOT_FOUND_PADDING_OR_ALGORITHM);
		} catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
			throw new EncryptionBadRequestException(INVALID_ALGORITHM_OR_KEY);
		} catch (IllegalBlockSizeException e) {
			throw new EncryptionBadRequestException(INVALID_BLOCKSIZE);
		} catch (BadPaddingException e) {
			throw new EncryptionBadRequestException(INVALID_PADDING);
		}
	}

	public static String aesCbcDecode(String encodedText) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(privateKey_256.getBytes(StandardCharsets.UTF_8),
				AES_ALGORITHM.getValue());
			IvParameterSpec iv = new IvParameterSpec(private_iv.getBytes());
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE.getValue());
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			byte[] decodedByte = Hex.decodeHex(encodedText.toCharArray());

			return new String(cipher.doFinal(decodedByte), StandardCharsets.UTF_8);
		} catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
			throw new EncryptionNotFoundException(NOT_FOUND_PADDING_OR_ALGORITHM);
		} catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
			throw new EncryptionBadRequestException(INVALID_ALGORITHM_OR_KEY);
		} catch (IllegalBlockSizeException e) {
			throw new EncryptionBadRequestException(INVALID_BLOCKSIZE);
		} catch (BadPaddingException e) {
			throw new EncryptionBadRequestException(INVALID_PADDING);
		} catch (DecoderException e) {
			throw new EncryptionBadRequestException(FAIL_TO_DECODER);
		}
	}

}
