package com.fixadate.global.exception.notFound;

import com.fixadate.global.exception.ExceptionCode;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 5.
 */
public non-sealed class S3ImgNotFound extends NotFoundException {
	public S3ImgNotFound(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}

}
