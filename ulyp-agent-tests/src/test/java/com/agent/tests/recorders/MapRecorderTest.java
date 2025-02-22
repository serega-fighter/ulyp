package com.agent.tests.recorders;

import com.agent.tests.util.AbstractInstrumentationTest;
import com.agent.tests.util.ForkProcessBuilder;
import com.ulyp.core.recorders.*;
import com.ulyp.storage.CallRecord;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class MapRecorderTest extends AbstractInstrumentationTest {

    @Test
    public void shouldRecordSimpleMap() {

        CallRecord root = runSubprocessAndReadFile(
                new ForkProcessBuilder()
                        .withMainClassName(TestCase.class)
                        .withMethodToRecord("returnHashMap")
                        .withRecordCollections(CollectionsRecordingMode.ALL)
        );

        MapRecord collection = (MapRecord) root.getReturnValue();

        Assert.assertEquals(2, collection.getSize());

        List<MapEntryRecord> entries = collection.getEntries();
        MapEntryRecord firstEntry = entries.get(0);
        StringObjectRecord key = (StringObjectRecord) firstEntry.getKey();
        Assert.assertEquals("a", key.value());
        StringObjectRecord value = (StringObjectRecord) firstEntry.getValue();
        Assert.assertEquals("b", value.value());
    }

    @Test
    public void shouldNotRecordCustomMapIfOnlyJavaMapsAreRecorded() {

        CallRecord root = runSubprocessAndReadFile(
                new ForkProcessBuilder()
                        .withMainClassName(TestCase.class)
                        .withMethodToRecord("returnHashMap")
                        .withRecordCollections(CollectionsRecordingMode.JAVA)
        );

        Assert.assertThat(root.getReturnValue(), Matchers.instanceOf(IdentityObjectRecord.class));
    }

    @Test
    public void shouldFallbackToIdentityIfRecordingFailed() {

        CallRecord root = runSubprocessAndReadFile(
                new ForkProcessBuilder()
                        .withMainClassName(TestCase.class)
                        .withMethodToRecord("returnMapThrowingOnIteration")
                        .withRecordCollections(CollectionsRecordingMode.ALL)
        );

        Assert.assertThat(root.getReturnValue(), Matchers.instanceOf(IdentityObjectRecord.class));
    }

    static class TestCase {

        public static Map<String, String> returnHashMap() {
            return new LinkedHashMap<String, String>() {
                {
                    put("a", "b");
                    put("c", "d");
                }
            };
        }

        public static Map<String, String> returnMapThrowingOnIteration() {
            return new HashMap<String, String>() {
                {
                    put("a", "b");
                    put("c", "d");
                }

                @Override
                public Set<Entry<String, String>> entrySet() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public static void main(String[] args) {
            System.out.println(returnHashMap());
            Map<String, String> stringStringMap = returnMapThrowingOnIteration();
            System.out.println(System.identityHashCode(stringStringMap));
        }
    }
}
