package com.edukit.core.notice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeCategory {
    ALL(1, "전체"),
    ANNOUNCEMENT(2, "공지"),
    EVENT(3, "이벤트");

    private final int id;
    private final String text;
}
