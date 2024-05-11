package com.fixadate.global.util.converter;

import com.fixadate.domain.googleCalendar.exception.EncryptionException;
import com.fixadate.global.util.AesUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Converter
@Slf4j
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
            log.info(e.getLocalizedMessage());
            log.info(e.getCause().toString());
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
            log.info(e.getLocalizedMessage());
            log.info(e.getCause().toString());
            throw new EncryptionException(e);
        }
    }

}
