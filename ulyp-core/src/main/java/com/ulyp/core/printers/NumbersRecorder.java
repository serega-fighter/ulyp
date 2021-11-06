package com.ulyp.core.printers;

import com.ulyp.core.ByIdTypeResolver;
import com.ulyp.core.Type;
import com.ulyp.core.TypeResolver;
import com.ulyp.core.TypeTrait;
import com.ulyp.core.printers.bytes.BinaryInput;
import com.ulyp.core.printers.bytes.BinaryOutput;

/**
 * Currently supports both primitive and boxed numbers
 */
public class NumbersRecorder extends ObjectRecorder {

    protected NumbersRecorder(byte id) {
        super(id);
    }

    @Override
    boolean supports(Type type) {
        return type.getTraits().contains(TypeTrait.NUMBER) || type.getTraits().contains(TypeTrait.PRIMITIVE);
    }

    @Override
    public ObjectRecord read(Type objectType, BinaryInput input, ByIdTypeResolver typeResolver) {
        return new NumberRecord(objectType, input.readString());
    }

    @Override
    public void write(Object object, Type objectType, BinaryOutput out, TypeResolver typeResolver) throws Exception {
        out.writeString(object.toString());
    }
}
