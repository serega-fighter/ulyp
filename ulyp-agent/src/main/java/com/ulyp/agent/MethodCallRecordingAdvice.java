package com.ulyp.agent;

import com.ulyp.agent.util.ByteBuddyTypeResolver;
import com.ulyp.core.Method;
import com.ulyp.core.MethodRepository;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

/**
 * Advice which instructs how to instrument methods. The byte buddy library copies the bytecode of methods into
 * constructors being instrumented.
 */
public class MethodCallRecordingAdvice {

    /**
     * @param methodId injected right into bytecode unique method id. Mapping is made by
     *                 {@link MethodIdFactory} class.
     */
    @Advice.OnMethodEnter
    static void enter(
            @MethodId int methodId,
            @Advice.Local("callId") long callId,
            @Advice.This(optional = true) Object callee,
            @Advice.AllArguments Object[] arguments) {

        // This if check is ugly, but the code is wired into bytecode, so it's more efficient to check right away instead of calling a method
        if (methodId >= MethodRepository.RECORD_METHODS_MIN_ID) {

            // noinspection UnusedAssignment local variable callId is used by exit() method
            callId = Recorder.getInstance().startOrContinueRecordingOnMethodEnter(
                    ByteBuddyTypeResolver.getInstance(),
                    MethodRepository.getInstance().get(methodId),
                    callee,
                    arguments
            );
        } else {

            if (Recorder.currentRecordingSessionCount.get() > 0 && Recorder.getInstance().recordingIsActiveInCurrentThread()) {
                //noinspection UnusedAssignment
                callId = Recorder.getInstance().onMethodEnter(
                        MethodRepository.getInstance().get(methodId),
                        callee,
                        arguments
                );
            }
        }
    }

    /**
     * @param methodId injected right into bytecode unique method id. Mapping is made by
     *                 {@link MethodIdFactory} class. Guaranteed to be the same
     *                 as for enter advice
     */
    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(
            @MethodId int methodId,
            @Advice.Local("callId") long callId,
            @Advice.Thrown Throwable throwable,
            @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {

        if (callId >= 0) {
            if (Recorder.currentRecordingSessionCount.get() > 0 && Recorder.getInstance().recordingIsActiveInCurrentThread()) {
                Recorder.getInstance().onMethodExit(
                        ByteBuddyTypeResolver.getInstance(),
                        MethodRepository.getInstance().get(methodId),
                        returnValue,
                        throwable,
                        callId
                );
            }
        }
    }
}
