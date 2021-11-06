package com.ulyp.core.recorders;

import com.ulyp.core.ByIdTypeResolver;
import com.ulyp.core.Type;
import com.ulyp.core.TypeResolver;
import com.ulyp.core.recorders.bytes.BinaryInput;
import com.ulyp.core.recorders.bytes.BinaryOutput;
import com.ulyp.core.recorders.bytes.BinaryOutputAppender;
import com.ulyp.core.util.ClassMatcher;

import java.util.HashSet;
import java.util.Set;

public class ToStringRecorder extends ObjectRecorder {

    private static final int TO_STRING_CALL_SUCCESS = 1;
    private static final int TO_STRING_CALL_FAIL = 0;

    private final Set<ClassMatcher> classesToPrintWithToString = new HashSet<>();

    protected ToStringRecorder(byte id) {
        super(id);
    }

    public void addClassNamesSupportPrinting(Set<ClassMatcher> classNames) {
        this.classesToPrintWithToString.addAll(classNames);
    }

    @Override
    boolean supports(Type type) {
        return classesToPrintWithToString.stream().anyMatch(x -> x.matches(type));
    }

    @Override
    public ObjectRecord read(Type objectType, BinaryInput input, ByIdTypeResolver typeResolver) {
        int result = input.readInt();
        if (result == TO_STRING_CALL_SUCCESS) {
            int identityHashCode = input.readInt();
            ObjectRecord printed = input.readObject(typeResolver);
            return new ToStringPrintedRecord(printed, objectType, identityHashCode);
        } else {
            return RecorderType.IDENTITY_RECORDER.getInstance().read(objectType, input, typeResolver);
        }
    }

    @Override
    public void write(Object object, Type classDescription, BinaryOutput out, TypeResolver typeResolver) throws Exception {
        try {
            String printed = object.toString();

            try (BinaryOutputAppender appender = out.appender()) {
                appender.append(TO_STRING_CALL_SUCCESS);
                appender.append(System.identityHashCode(object));
                appender.append(printed, typeResolver);
            }
        } catch (Throwable e) {
            try (BinaryOutputAppender appender = out.appender()) {
                appender.append(TO_STRING_CALL_FAIL);
                RecorderType.IDENTITY_RECORDER.getInstance().write(object, appender, typeResolver);
            }
        }
    }
}
