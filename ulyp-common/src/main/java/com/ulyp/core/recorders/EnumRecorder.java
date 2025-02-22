package com.ulyp.core.recorders;

import com.ulyp.core.ByIdTypeResolver;
import com.ulyp.core.Type;
import com.ulyp.core.TypeResolver;
import com.ulyp.core.recorders.bytes.BinaryInput;
import com.ulyp.core.recorders.bytes.BinaryOutput;
import org.jetbrains.annotations.NotNull;

public class EnumRecorder extends ObjectRecorder {

    protected EnumRecorder(byte id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.isEnum();
    }

    @Override
    public ObjectRecord read(@NotNull Type type, BinaryInput input, ByIdTypeResolver typeResolver) {
        return new EnumRecord(type, input.readString());
    }

    @Override
    public void write(Object object, @NotNull Type objectType, BinaryOutput out, TypeResolver typeResolver) throws Exception {
        out.writeString(((Enum<?>) object).name());
    }
}
