package com.farpost.ldt;

import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static com.farpost.ldt.TestUtils.near;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PojoTaskTest {

  @Test
  public void taskShouldAbleToInjectParameters() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    PojoTask<SomeTask> task = new PojoTask<SomeTask>(SomeTask.class, "doWork");

    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("string", "foo");
    parameters.put("integer", "6");
    task.setParameters(parameters);

    SomeTask t = task.getTaskObject();
    assertThat(t.getString(), equalTo("foo"));
    assertThat(t.getInteger(), equalTo(6));
  }

  @Test
  public void pojoTaskCanBeRunWithTestRunner()
    throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
    InterruptedException {
    PojoTask<?> task = new PojoTask(SleepTask.class, "execute");

    TestRunner runner = new TestRunner();
    runner.setConcurrencyLevel(2);
    runner.setThreadSamplesCount(5);

    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("delay", "200");
    task.setParameters(parameters);

    TestResult result = runner.run(task);
    assertThat(result.getThroughput(), near(10f, 2f));
  }

  public static class SomeTask {

    private String string;
    private int integer;

    public String getString() {
      return string;
    }

    public void setString(String string) {
      this.string = string;
    }

    public int getInteger() {
      return integer;
    }

    public void setInteger(int integer) {
      this.integer = integer;
    }

    public void doWork() {}
  }
}
