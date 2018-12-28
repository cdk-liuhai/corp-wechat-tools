package com.yangjing.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yangjing.tools.aam.HttpRequestAAMTool;
import com.yangjing.tools.main.ContactsSynchronized;
import com.yangjing.tools.model.enums.URLData;
import com.yangjing.tools.utils.HttpRequestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class CorpWechatToolsApplicationTests {

	@Test
	public void contextLoads() {
	}

	//获取学校组织架构接口
	@Test
	public void testAAM_480(){
		String url= URLData.URL_480.getUrl();
		String jsonString="{\n" +
				"    \"orgaId\": \"0748e0947a564483a002ddc3e98b2d4c\",\n" +
				"    \"pathLength\": 1,\n" +
				"    \"deptId\": \"\"\n" +
				"}\n";
		String jsonStr=HttpRequestUtil.post(url,jsonString);
		System.out.println(jsonStr);
	}

	//获取学校部门直属成员接口
	@Test
	public void testAAM_483(){
		String url=URLData.URL_483.getUrl();
		String jsonString="{\n" +
				"    \"orgaId\": \"0748e0947a564483a002ddc3e98b2d4c\",\n" +
				"    \"deptId\": \"efa699d2175944d2870a85b0cd1b11fd\",\n" +
				"    \"start\": 0,\n" +
				"    \"end\": 10\n" +
				"}";
		String jsonStr=HttpRequestUtil.post(url,jsonString);
		System.out.println(jsonStr);
	}

	@Test
	public void testAAM_8(){
//		String jsonString="{\n" +
//				"    \"orgaids\":[\"0748e0947a564483a002ddc3e98b2d4c\"]\n" +
//				"}";
		JSONObject param=new JSONObject();
		JSONArray jsonArray=new JSONArray();
		jsonArray.add("0748e0947a564483a002ddc3e98b2d4c");
		param.put("orgaids",jsonArray);
		System.out.println(param.toJSONString());
		System.out.println(HttpRequestUtil.post(URLData.URL_8.getUrl(),param.toJSONString()));
	}

	@Test
	public void test_getOrganization(){
		System.out.println(ContactsSynchronized.getOrganization(URLData.URL_8.getUrl(),"0748e0947a564483a002ddc3e98b2d4c"));
	}

	@Test
    public void testAAM_485(){
        //查询学校不属于部门的成员
        JSONObject param=new JSONObject();
        param.put("orgaId","0748e0947a564483a002ddc3e98b2d4c");
        JSONObject resJson= HttpRequestAAMTool.invoke_AAMInterface(URLData.URL_485.getUrl(),param);
        System.out.println(resJson);
    }

    @Test
    public void testAAM_486(){
        //查询成员所在部门列表
        JSONObject param=new JSONObject();
        param.put("personid","23b6cf772b364171a0873e4693a86aef");
        JSONObject resJson= HttpRequestAAMTool.invoke_AAMInterface(URLData.URL_486.getUrl(),param);
        System.out.println(resJson);
    }

}

