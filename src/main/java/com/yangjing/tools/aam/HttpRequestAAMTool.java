package com.yangjing.tools.aam;

import com.alibaba.fastjson.JSONObject;
import com.yangjing.tools.utils.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpRequestAAMTool {

    /**
     * 调用aam接口
     * @param url
     * @param jsonObject
     * @return
     */
    public static JSONObject invoke_AAMInterface(String url,JSONObject jsonObject){
        String jsonString=HttpRequestUtil.post(url,jsonObject.toJSONString());
        JSONObject resJson=JSONObject.parseObject(jsonString);
        if(resJson.getString("retCode").equals("000000")){
            log.info("调用成功",resJson);
            return resJson;
        }else{
            log.error("调用失败："+resJson.getString("retDesc"),resJson);
            return null;
        }
    }

    /**
     * 根据组织id、部门id和 组织层级
     * 查询当前组织下指定部门和指定层级下的子部门信息
     * @param url
     * @param orgaId
     * @param pathLength
     * @param deptId  级别大于1时，deptId必填
     * @return
     */
    public static JSONObject getDepartmentInfoByLevel(String url,String orgaId,String pathLength,String deptId){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("orgaId",orgaId);
        jsonObject.put("pathLength",pathLength);
        jsonObject.put("deptId",deptId);
        String jsonString= HttpRequestUtil.post(url,jsonObject.toJSONString());
        JSONObject resJson=JSONObject.parseObject(jsonString);
        if(resJson.getString("retCode").equals("000000")){
            log.info("获取部门信息成功",resJson);
            return resJson;
        }else{
            log.error("获取部门信息失败："+resJson.getString("retDesc"),resJson);
            return null;
        }
    }

    /**
     * 根据组织id和部门id，获取当前部门下的直属成员信息
     * @param url
     * @param orgaId
     * @param deptId
     * @param start 非必填，默认0
     * @param end   非必填，默认10
     * @return
     */
    public static JSONObject getMemberInfo(String url,String orgaId,String deptId,Integer start,Integer end){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("orgaId",orgaId);
        jsonObject.put("deptId",deptId);
        if(start!=null && end!=null){
            jsonObject.put("start",start);
            jsonObject.put("end",end);
        }
        String jsonString= HttpRequestUtil.post(url,jsonObject.toJSONString());
        JSONObject resJson=JSONObject.parseObject(jsonString);
        if(resJson.getString("retCode").equals("000000")){
            log.info("获取成员信息成功",resJson);
            return resJson;
        }else{
            log.error("获取成员信息失败："+resJson.getString("retDesc"),resJson);
            return null;
        }
    }
}
