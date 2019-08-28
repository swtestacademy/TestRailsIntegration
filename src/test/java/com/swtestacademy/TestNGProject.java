package com.swtestacademy;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class TestNGProject
{
	String PROJECT_ID = "1";
	APIClient client = null;

	@BeforeSuite
	public void createSuite(ITestContext ctx) throws IOException, APIException {
		client = new APIClient("https://swtestacademy.testrail.io");
		client.setUser("canberkakduygu@gmail.com");
		client.setPassword("Qwerty_123");
		Map data = new HashMap();
		data.put("include_all",true);
		data.put("name","Test Run "+System.currentTimeMillis());
		JSONObject c = null;
		c = (JSONObject)client.sendPost("add_run/"+PROJECT_ID,data);
		Long suite_id = (Long)c.get("id");
		ctx.setAttribute("suiteId",suite_id);


	}

	@BeforeMethod
	public void beforeTest(ITestContext ctx,Method method) throws NoSuchMethodException {
		Method m = TestNGProject.class.getMethod(method.getName());

		if (m.isAnnotationPresent(TestRails.class)) {
			TestRails ta = m.getAnnotation(TestRails.class);
			System.out.println(ta.id());
			ctx.setAttribute("caseId",ta.id());
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

    @AfterMethod
	public void afterTest(ITestResult result, ITestContext ctx) throws IOException, APIException {
		Map data = new HashMap();
		if(result.isSuccess()) {
			data.put("status_id",1);
		}
		else {
			data.put("status_id", 5);
			data.put("comment", result.getThrowable().toString());
		}

		String caseId = (String)ctx.getAttribute("caseId");
		Long suiteId = (Long)ctx.getAttribute("suiteId");
		client.sendPost("add_result_for_case/"+suiteId+"/"+caseId,data);

	}
}
