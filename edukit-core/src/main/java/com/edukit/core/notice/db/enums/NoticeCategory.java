package com.edukit.core.notice.db.enums;

import com.edukit.core.notice.exception.NoticeException;
import com.edukit.core.notice.exception.NoticeErrorCode;
import java.util.Arrays;
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

    public static NoticeCategory from(final String name) {
        if (name == null || name.isBlank()) {
            return ALL;
        }
        final String normalized = name.trim();
        return Arrays.stream(NoticeCategory.values())
                .filter(category -> category.name().equalsIgnoreCase(normalized))
                .findFirst()
                .orElseThrow(() -> new NoticeException(NoticeErrorCode.INVALID_NOTICE_CATEGORY));
    }

    public boolean isCreatable() {
        return this != ALL;
    }
}
