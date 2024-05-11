package com.fixadate.global.util.converter;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;

import com.fixadate.domain.googleCalendar.exception.EncryptionException;
import com.fixadate.global.util.AesUtil;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (attribute.isBlank()) {
			return attribute;
		}
		try {
			return AesUtil.aesCBCEncode(attribute);
		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
				 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new EncryptionException(e);
		}
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		if (dbData.isBlank()) {
			return dbData;
		}
		try {
			return AesUtil.aesCBCDecode(dbData);
		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
				 InvalidKeyException | DecoderException | IllegalBlockSizeException | BadPaddingException e) {
			throw new EncryptionException(e);
		}
	}

}
