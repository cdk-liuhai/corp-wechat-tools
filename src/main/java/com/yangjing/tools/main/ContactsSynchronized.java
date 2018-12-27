package com.yangjing.tools.main;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yangjing.tools.aam.HttpRequestAAMTool;
import com.yangjing.tools.model.URLData;
import com.yangjing.tools.wechat.ContactsManager;
import com.yangjing.tools.wechat.HttpRequestWeChatTool;
import lombok.extern.slf4j.Slf4j;

import java.util.Stack;

/**
 * @author yangjing
 * @create 2018-12-27 11:48:48
 * @version 1.0
 * @des 同步aam通讯录到企业微信通讯录中
 */
@Slf4j
public class ContactsSynchronized {

    public static void main(String[] args) {
        String orgaId="0748e0947a564483a002ddc3e98b2d4c";
        ContactsManager contactsManager=new ContactsManager();

        //获取机构详情
        JSONObject resJson=getOrganization(URLData.URL_8.getUrl(),orgaId);
        if(resJson!=null){
            JSONObject root=resJson.getJSONArray("orgalist").getJSONObject(0);
            //创建组织机构，作为根节点
            int rootId=contactsManager.createDepartment(URLData.WHCHAT_create_department.getUrl(),root.getString("name"),0,null,null);
            //获取一级部门节点
//            JSONArray jsonArray=json.getJSONArray("deptList");
//            for(int i=0;i<jsonArray.size();i++){
//                JSONObject jsonObject=jsonArray.getJSONObject(i);
//                contactsManager.createDepartment(URLData.WHCHAT_create_department.getUrl(),jsonObject.getString("name"),rootId,null,null);
//            }
//            Stack<JSONObject> stack=new Stack<>();
//            while(!stack.empty()){
//                //获取机构的一级部门
//                JSONObject jsonObject= HttpRequestAAMTool.getDepartmentInfoByLevel(URLData.URL_480.getUrl(),orgaId,"1","");
//            }
        }else{
            log.error("获取组织信息失败",resJson);
        }
    }

    /**
     * 根据orgaId获取组织机构详情
     * @param url
     * @param orgaId
     * @return
     */
    public static JSONObject getOrganization(String url,String orgaId){
        JSONObject param=new JSONObject();
        param.put("orgaId",new JSONArray().add(orgaId));
        return HttpRequestAAMTool.invoke_AAMInterface(url,param);
    }
}
