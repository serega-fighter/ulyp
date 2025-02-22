package com.agent.tests.libs;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.agent.tests.libs.util.hibernate.ApplicationConfiguration;
import com.agent.tests.libs.util.hibernate.Department;
import com.agent.tests.libs.util.hibernate.DepartmentService;
import com.agent.tests.libs.util.hibernate.Person;
import com.agent.tests.util.AbstractInstrumentationTest;
import com.agent.tests.util.DebugCallRecordTreePrinter;
import com.agent.tests.util.ForkProcessBuilder;
import com.agent.tests.util.RecordingResult;
import com.ulyp.core.util.MethodMatcher;
import com.ulyp.storage.CallRecord;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class HibernateRecordingTest extends AbstractInstrumentationTest {

    @Test
    public void testSaveEntityWithHibernate() {

        RecordingResult recordingResult = runSubprocess(
                new ForkProcessBuilder()
                        .withMainClassName(HibernateSaveEntityTest.class)
                        .withMethodToRecord(MethodMatcher.parse("**.HibernateSaveEntityTest.main"))
                        .withInstrumentedPackages()
                        .withRecordConstructors()
        );

        CallRecord singleRoot = recordingResult.getSingleRoot();
        String assertMsg = DebugCallRecordTreePrinter.printTree(singleRoot);

        assertThat(assertMsg, singleRoot.getSubtreeSize(), greaterThan(15000));
    }

    public static class HibernateSaveEntityTest {

        public static void main(String[] args) throws Exception {
            ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
            DepartmentService departmentService = context.getBean(DepartmentService.class);

            Department department = new Department();
            for (int i = 0; i < 5; i++) {

                Person p = new Person();
                p.setFirstName("Name" + i);
                p.setLastName("LastName" + i);
                p.setPhoneNumber(String.valueOf(ThreadLocalRandom.current().nextInt()));
                p.setAge(ThreadLocalRandom.current().nextInt(100));

                department.getPeople().add(p);
            }

            departmentService.save(department);

            List<Department> allDepartments = departmentService.findAll();

            Assert.assertEquals(1, allDepartments.size());

            Department departmentFromDb = allDepartments.get(0);
            Set<Person> people = departmentFromDb.getPeople();
            for (Person p : people) {
                Assert.assertNotNull(p.getId());
            }
        }
    }
}
