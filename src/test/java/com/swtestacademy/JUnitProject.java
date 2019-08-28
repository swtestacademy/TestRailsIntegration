package com.swtestacademy;

import org.json.simple.JSONObject;
import org.junit.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.junit.runner.Description;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */

public class JUnitProject
{
	private static String PROJECT_ID = "1";
	private static APIClient client = null;
	private static Long runId;
	private static String caseId = "";
	private static int FAIL_STATE = 5;
	private static int SUCCESS_STATE = 1;
	@Rule
	public TestName testName = new TestName();

	@BeforeClass
	public static void createSuite() throws IOException, APIException {
		//Login to API
		client = new APIClient("https://swtestacademy.testrail.io");
		client.setUser("canberkakduygu@gmail.com");
		client.setPassword("Qwerty_123");
		//Create Test Run
		Map data = new HashMap();
		data.put("include_all",true);
		data.put("name","Test Run "+System.currentTimeMillis());
		JSONObject c = (JSONObject)client.sendPost("add_run/"+PROJECT_ID,data);
		//Extract Test Run Id
		runId = (Long)c.get("id");

	}

	@Before
	public void beforeTest() throws NoSuchMethodException {
		Method m = JUnitProject.class.getMethod(testName.getMethodName());
		if (m.isAnnotationPresent(TestRails.class)) {
			TestRails ta = m.getAnnotation(TestRails.class);
			caseId = ta.id();
		}
	}
    @TestRails(id="1")
    @Test
    public void validLogin()
    {
		Assert.assertTrue(true);
    }

    @TestRails(id="2")
    @Test
    public void invalidLogin()
    {
		Assert.assertTrue(false);
    }

	@Rule
	public final TestRule watchman = new TestWatcher() {
		Map data = new HashMap();

		@Override
		public Statement apply(Statement base, Description description) {
			return super.apply(base, description);
		}

		@Override
		protected void succeeded(Description description) {
			data.put("status_id", SUCCESS_STATE);
		}

		// This method gets invoked if the test fails for any reason:
		@Override
		protected void failed(Throwable e, Description description) {
			data.put("status_id", FAIL_STATE);
		}

		// This method gets called when the test finishes, regardless of status
		// If the test fails, this will be called after the method above
		@Override
		protected void finished(Description description) {
			try {
				client.sendPost("add_result_for_case/" + runId + "/" + caseId, data);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (APIException e) {
				e.printStackTrace();
			}
		}

		;
	};

}
