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

    public static ClubException memberNotInClub() {
        return new ClubException(HttpStatus.FORBIDDEN, "클럽 멤버가 아닙니다.");
    }

    public static ClubException notClubLeader() {
        return new ClubException(HttpStatus.FORBIDDEN, "클럽장만 수정할 수 있습니다.");
    }

    public static ClubException clubLeaderCannotLeave() {
        return new ClubException(HttpStatus.BAD_REQUEST, "클럽장은 클럽을 탈퇴할 수 없습니다.");
    }

    public static ClubException ClubLeaderNotFoundException() {
        return new ClubException(HttpStatus.INTERNAL_SERVER_ERROR, "클럽 리더가 설정되지 않았습니다.");
    }
}
