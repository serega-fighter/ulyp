package com.ulyp.core.recorders.bytes;

import com.ulyp.transport.BinaryRecordedExitMethodCallEncoder;
import org.agrona.concurrent.UnsafeBuffer;

public class BinaryOutputForExitRecordImpl extends AbstractBinaryOutput {

    private BinaryRecordedExitMethodCallEncoder encoder;

    public void wrap(BinaryRecordedExitMethodCallEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void write(UnsafeBuffer unsafeBuffer, int length) {
        int headerLength = 4;
        final int limit = encoder.limit();
        encoder.limit(limit + headerLength + length);
        encoder.buffer().putInt(limit, length, java.nio.ByteOrder.LITTLE_ENDIAN);
        encoder.buffer().putBytes(limit + headerLength, unsafeBuffer, 0, length);
    }
}
