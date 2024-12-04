package net.pointofviews.club.exception;

import net.pointofviews.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ClubException extends BusinessException {

    public ClubException(HttpStatus status, String message) {
        super(status, message);
    }

    public static ClubException clubNotFound(UUID clubId) {
        return new ClubException(HttpStatus.NOT_FOUND, String.format("클럽(Id: %s)이 존재하지 않습니다.", clubId));
    }
}