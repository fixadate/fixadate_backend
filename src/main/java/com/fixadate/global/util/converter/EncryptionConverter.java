package com.fixadate.global.util.converter;

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
		return AesUtil.aesCBCEncode(attribute);
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		if (dbData.isBlank()) {
			return dbData;
		}
		return AesUtil.aesCBCDecode(dbData);
	}

}
