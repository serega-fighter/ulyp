package com.agent.tests.general;

import com.agent.tests.general.a.A;
import com.agent.tests.general.a.c.C;
import com.agent.tests.util.AbstractInstrumentationTest;
import com.agent.tests.util.ForkProcessBuilder;
import com.ulyp.storage.CallRecord;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PackageFilterInstrumentationTest extends AbstractInstrumentationTest {

    @Test
    public void shouldInstrumentAndTraceAllClasses() {
        CallRecord root = runSubprocessAndReadFile(
                new ForkProcessBuilder()
                        .withMainClassName(A.class)
                        .withInstrumentedPackages("com.agent.tests.general.a")
        );

        assertThat(root.getMethod().getName(), is("main"));
        assertThat(root.getMethod().getDeclaringType().getName(), is(A.class.getName()));
        assertThat(root.getChildren(), Matchers.hasSize(2));
    }

    @Test
    public void shouldExcludeInstrumentationPackage() {
        CallRecord root = runSubprocessAndReadFile(
                new ForkProcessBuilder()
                        .withMainClassName(A.class)
                        .withInstrumentedPackages("com.agent.tests.general.a")
                        .withExcludedFromInstrumentationPackages("com.agent.tests.general.a.b")
        );

        assertThat(root.getMethod().getName(), is("main"));
        assertThat(root.getMethod().getDeclaringType().getName(), is("com.agent.tests.general.a.A"));
        assertThat(root.getChildren(), Matchers.hasSize(1));

        CallRecord callRecord = root.getChildren().get(0);

        assertThat(callRecord.getMethod().getDeclaringType().getName(), is(C.class.getName()));
        assertThat(callRecord.getMethod().getName(), is("c"));
    }

    @Test
    public void shouldExcludeTwoPackages() {
        CallRecord root = runSubprocessAndReadFile(
                new ForkProcessBuilder()
                        .withMainClassName(A.class)
                        .withInstrumentedPackages("com.agent.tests.general.a")
                        .withExcludedFromInstrumentationPackages("com.agent.tests.general.a.b", "com.agent.tests.general.a.c")
        );

        assertThat(root.getMethod().getName(), is("main"));
        assertThat(root.getMethod().getDeclaringType().getName(), is("com.agent.tests.general.a.A"));
        assertThat(root.getChildren(), Matchers.empty());
    }
}
