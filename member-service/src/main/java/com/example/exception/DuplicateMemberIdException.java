package com.example.exception;

public class DuplicateMemberIdException extends BusinessException {
    public DuplicateMemberIdException() {
        super(ErrorCode.DUPLICATE_MEMBER_ID);
    }
}
