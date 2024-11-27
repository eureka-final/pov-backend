package net.pointofviews.curation.exception;

import org.springframework.http.HttpStatus;
import net.pointofviews.common.exception.BusinessException;

public class CurationNotFoundException extends BusinessException {
    public CurationNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 큐레이션입니다.");
    }
}