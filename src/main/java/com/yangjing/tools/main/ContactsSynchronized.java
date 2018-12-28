package com.yangjing.tools.main;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yangjing.tools.aam.HttpRequestAAMTool;
import com.yangjing.tools.model.domain.User;
import com.yangjing.tools.model.enums.URLData;
import com.yangjing.tools.model.enums.WeChatData;
import com.yangjing.tools.utils.HttpRequestUtil;
import com.yangjing.tools.wechat.ContactsManager;
import com.yangjing.tools.wechat.HttpRequestWeChatTool;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author yangjing
 * @create 2018-12-27 11:48:48
 * @version 1.0
 * @des 同步aam通讯录到企业微信通讯录中
 */
@Slf4j
public class ContactsSynchronized {

    public static ContactsManager contactsManager=new ContactsManager();

    public static void main(String[] args) {
        String orgaId="0748e0947a564483a002ddc3e98b2d4c";
        //启动微信请求accessToken线程，每隔7200s重新请求一次Token
        HttpRequestWeChatTool.scheduleUpdateAccessToken(WeChatData.corporationId.getValue(),WeChatData.contacts_secret.getValue());

        //获取机构详情
        JSONObject resJson=getOrganization(URLData.URL_8.getUrl(),orgaId);
        if(resJson!=null) {
            JSONObject root = resJson.getJSONArray("orgalist").getJSONObject(0);
            //创建组织机构，作为根节点
            int rootId = contactsManager.createDepartment(URLData.WHCHAT_create_department.getUrl(), root.getString("organame"), 1, null, null);
            root.put("id", rootId);
            root.put("deptId", "");
            root.put("pathLength",0);
            Stack<JSONObject> stack = new Stack<>();
            Map<String,Integer> map=new HashMap<>();
            Set<User> userSet=new TreeSet<User>(new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.getPersonid().compareTo(o2.getPersonid());
                }
            });
            stack.push(root);
            //利用栈后进先出的特性，深度优先遍历部门列表
            while (!stack.empty()) {
                JSONObject parent = stack.pop();
                map.put(parent.getString("deptId"),parent.getInteger("id"));
                //获取部门直属成员
                resJson=HttpRequestAAMTool.getMemberInfo(URLData.URL_483.getUrl(),orgaId,parent.getString("deptId"),null,null);
                if(resJson!=null){
                    userSet.addAll(resJson.getJSONObject("memberInfo").getJSONArray("userList").toJavaList(User.class));
                }
                //查询子部门节点
                JSONObject jsonObject=null;
                if(parent.getString("deptId").equals("") || parent.getString("hasLeaf").equals("yes")){
                     jsonObject= HttpRequestAAMTool.getDepartmentInfoByLevel(URLData.URL_480.getUrl(), orgaId, parent.getInteger("pathLength")+1, parent.getString("deptId"));
                }
                if (jsonObject != null && jsonObject.getJSONArray("deptList") != null) {
                    JSONArray childrens = jsonObject.getJSONArray("deptList");
                    //在企业微信通讯录中，创建子部门
                    for (int i = 0; i < childrens.size(); i++) {
                        JSONObject children = childrens.getJSONObject(i);
                        int id=contactsManager.createDepartment(URLData.WHCHAT_create_department.getUrl(), children.getString("name"),parent.getInteger("id") , null, null);
                        children.put("id",id);
                        stack.push(children);
                    }
                }
            }
            /*--------------------------创建成员-----------------------------*/
            //查询学校不属于部门的成员
            JSONObject param=new JSONObject();
            param.put("orgaId",orgaId);
            resJson=HttpRequestAAMTool.invoke_AAMInterface(URLData.URL_485.getUrl(),param);
            if(resJson!=null && resJson.getJSONArray("userList")!=null){
                userSet.addAll(resJson.getJSONArray("userList").toJavaList(User.class));
            }
            for(User user : userSet){
                //查询成员属于哪些部门
                param=new JSONObject();
                param.put("personid",user.getPersonid());
                resJson=HttpRequestAAMTool.invoke_AAMInterface(URLData.URL_486.getUrl(),param);
                if(resJson!=null && resJson.getJSONArray("data")!=null){
                    JSONArray jsonArray=resJson.getJSONArray("data");
                    JSONArray department=new JSONArray();
                    for(int i=0;i<jsonArray.size();i++){
                        String deptId=jsonArray.getJSONObject(i).getString("deptId");
                        if(map.containsKey(deptId)){
                            department.add(map.get(deptId));
                        }
                    }
                    //如果该成员不属于任何部门，则他属于学校机构下
                    if(jsonArray.size()==0){
                        department.add(map.get(""));
                    }
                    //创建该成员
                    createDepartmentMember(user,department);
                }
            }
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
        JSONArray jsonArray=new JSONArray();
        jsonArray.add(orgaId);
        param.put("orgaids",jsonArray);
        String jsonString=HttpRequestUtil.post(URLData.URL_8.getUrl(),param.toJSONString());
        JSONObject resJson=JSONObject.parseObject(jsonString);
        if(resJson!=null && resJson.getString("result").equals("000000")){
            log.info("调用成功",resJson);
            return resJson;
        }else{
            log.error("调用失败："+resJson.getString("desc"),resJson);
            return null;
        }
    }

    /**
     * 将aam通讯录指定部门的直属成员
     * 导入微信通讯录相同部门中
     * @param user
     * @param department
     */
    public static void createDepartmentMember(User user,JSONArray department){
                JSONObject member=new JSONObject();
                member.put("userid",user.getAccount());
                member.put("name",user.getName());
                member.put("email",generateRandomEmail(user.getAccount(),"whty.com.cn"));
                member.put("to_invite",false);      //不通知该成员使用企业微信，若开启，会每日通过短信或邮箱邀请该成员使用企业微信，持续3日
                member.put("department",department);
                contactsManager.createMember(URLData.WHCHAT_create_member.getUrl(),member);
    }

    public static String generateRandomEmail(String acountId,String emailSuffix){
        return acountId+"@"+emailSuffix;
    }

}
