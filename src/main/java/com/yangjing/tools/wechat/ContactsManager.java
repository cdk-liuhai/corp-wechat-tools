package com.yangjing.tools.wechat;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContactsManager {

    /**
     * 调用企业微信api，创建部门
     * @param url
     * @param name
     * @param parentid
     * @param order 非必填
     * @param id    非必填
     * @return 部门id
     */
    public Integer createDepartment(String url,String name,int parentid,Integer order,Integer id){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("name",name);
            jsonObject.put("parentid",parentid);
            if(order!=null && id!=null){
                jsonObject.put("order",order);
                jsonObject.put("id",id);
            }
            return HttpRequestWeChatTool.invokeInterface(url,jsonObject).getInteger("id");
    }

}
