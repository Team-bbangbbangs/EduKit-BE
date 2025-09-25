package com.edukit.core.common.converter;


import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentRecordTypeConverter implements Converter<String, StudentRecordType> {

    @Override
    public StudentRecordType convert(final String source) {
        return StudentRecordType.from(source);
    }
}
