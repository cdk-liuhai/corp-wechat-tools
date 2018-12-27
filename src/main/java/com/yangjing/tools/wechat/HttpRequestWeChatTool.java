package com.yangjing.tools.wechat;

import com.alibaba.fastjson.JSONObject;
import com.yangjing.tools.utils.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


/**
 * @author yangjing
 * @create 2018-12-27 09:42:28
 * @version 1.0
 */
@Slf4j
public class HttpRequestWeChatTool {

    public static String accessToken=null;

    /**
     * 定时重新获取accessToken
     * @param corpId
     * @param corpSecretId
     */
    public static void scheduleUpdateAccessToken(String corpId,String corpSecretId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(true){
                        accessToken=null;
                        JSONObject jsonObject=getAccessToken(corpId,corpSecretId);
                        if(jsonObject!=null){
                            accessToken=jsonObject.getString("access_token");
                            int expiresIn=jsonObject.getInteger("expires_in");
                            Thread.sleep(expiresIn * 1000); //获取token成功后，按照微信服务器给出的过期时间，休眠请求token线程
                        }else{
                            Thread.sleep(3 * 1000); //如果jsonObject为null，表示获取token失败，尝试3s后重新发送请求
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    log.error("获取Token时出现错误："+e.getMessage(),e);
                }
            }
        }).start();
    }

    /**
     * 发起请求，获取accessToken
     * @param corpId
     * @param corpSecretId
     * @return
     */
    public static JSONObject getAccessToken(String corpId,String corpSecretId) {
        if(StringUtils.isNoneBlank(corpId) && StringUtils.isNoneBlank(corpSecretId)){
            String url=" https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+corpId+"&corpsecret="+corpSecretId;
            String jsonString= HttpRequestUtil.get(url);
            JSONObject jsonObject=JSONObject.parseObject(jsonString);
            if(jsonObject!=null && jsonObject.getInteger("errcode")==0){
                log.info("获取accessToken成功",jsonObject);
                return jsonObject;
            }else{
                log.error("获取accessToken失败："+jsonObject.getString("errmsg"),jsonObject);
                return null;
            }
        }
        return null;
    }

    /**
     * 通过accessToken，调用企业微信接口
     * @param url
     * @param jsonObject
     * @return
     */
    public static JSONObject invokeInterface(String url,JSONObject jsonObject){
        if(accessToken==null){
            log.info("token还在获取中，请稍后重试（大约1s以内）");
            return null;
        }else{
            jsonObject.put("access_token",accessToken);
            String jsonString=HttpRequestUtil.post(url,jsonObject.toJSONString());
            JSONObject resJson=JSONObject.parseObject(jsonString);
            if(resJson.getInteger("errcode")==0){
                log.info("调用成功",resJson);
                return resJson;
            }else{
                log.error("调用失败："+resJson.getString("errmsg"),resJson);
                return null;
            }
        }
    }
}
