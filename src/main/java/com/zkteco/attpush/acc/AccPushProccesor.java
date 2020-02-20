package com.zkteco.attpush.acc;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iclock")
/**
 * 1.初始化
 * 2.注册
 * 3.push
 * 4.心跳
 * 5.心跳处理结果
 * 6.实时记录
 */
public class AccPushProccesor {
    /**
     * 处理初始化请求的
     * @return
     */
    @RequestMapping(value = "/cdata",method = RequestMethod.GET)
    public String init(String SN,String pushver,String options,HttpServletRequest req){
//        System.out.println("#######get,cdata请求的URL:"+req.getServletPath());
        Map<String,String> param = convertMap(req);
//        System.out.println("######get,cdata请求的参数"+param.toString());
//        System.out.println("######get,cdata请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        System.out.println("SN..."+SN+"...pushver..."+pushver+"...options..."+options+"..."+new SimpleDateFormat("HH:mm:ss").format(new Date()));

        return "OK";
    }

    /**
     * 处理注册请求的
     * @return
     */
    @RequestMapping("/registry")
    public String registry(@RequestBody String deviceData,HttpServletRequest req){
//        System.out.println("#######registry请求的URL:"+req.getServletPath());
//        Map<String,String> param = convertMap(req);
//        System.out.println("######registry请求的参数"+param.toString());
//        System.out.println("######registry请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
//        System.out.println(deviceData);
        return "RegistryCode=Uy47fxftP3";
    }

    /**
     * 处理push请求
     *
     * @return
     */
    @RequestMapping(value = "/push")
    public String push(HttpServletRequest req){
        System.out.println("进入到push请求.....");
        System.out.println("#######push请求的URL:"+req.getServletPath());
        Map<String,String> param = convertMap(req);
        System.out.println("######push请求的参数"+param.toString());
        System.out.println("######push请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        StringBuffer sb = new StringBuffer();
        sb.append("ServerVersion=3.0.1\n");
        sb.append("ServerName=ADMS\n");
        sb.append("PushVersion=3.0.1\n");
        sb.append("ErrorDelay=30\n");
        sb.append("RequestDelay=5\n");
        sb.append("TransTimes=00:0014:00\n");
        sb.append("TransInterval=1\n");
        sb.append("TransTables=User Transaction\n");
        sb.append("Realtime=1\n");
        sb.append("SessionID=30BFB04B2C8AECC72C01C03BFD549D15\n");
        sb.append("TimeoutSec=10\n");
        return sb.toString();
    }

    /*
     * 处理心跳请求
     *
     */
    @RequestMapping("/getrequest")
    public String heartbeat(HttpServletRequest req){
//        System.out.println("#######getrequest请求的URL:"+req.getServletPath());
        Map<String,String> param = convertMap(req);
//        System.out.println("######getrequest请求的参数"+param.toString());
//        System.out.println("######getrequest请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        System.out.println("进入到心跳请求....");
        //cmd.txt 放在d盘
        BufferedReader br = null;
        File file = new File("d://cmd.txt");
        StringBuffer sb = new StringBuffer();
        if(file.exists()){
            try {
                br = new BufferedReader(new FileReader(file));
                String cmd = "";
                while((cmd = br.readLine())!=null){
                    if(cmd.startsWith("C")){
                        //正常的命令
                        sb.append(cmd+"\r\n\r\n");
                    }else if(cmd.startsWith("D")){
                    }else{
                        //进到这里目前看只能是E开头，则表示人员头像
                    }
                }
                br.close();
                file.delete();
                Thread.sleep(1000);
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            return "OK";
        }
            return "OK";
    }

    public Map<String, String> convertMap(HttpServletRequest request) {
        Map<String, String> returnMap = new HashMap<>();
        // 转换为Entry
        Set<Map.Entry<String, String[]>> entries = request.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String key = entry.getKey();
            StringBuffer value = new StringBuffer("");
            String[] val = entry.getValue();
            if (null != val && val.length > 0) {
                for (String v:val) {
                    value.append(v);
                }
            }
            returnMap.put(key, value.toString());
        }
        return returnMap;
    }

    @RequestMapping("/ping")
    public String ping(HttpServletRequest req){
        System.out.println("设备上传时通过Ping请求维持心跳");
//        System.out.println("#######ping请求的URL:"+req.getServletPath());
        Map<String,String> param = convertMap(req);
//        System.out.println("######ping请求的参数"+param.toString());
//        System.out.println("######ping请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        return "OK";
    }

    @RequestMapping(value = "/cdata",method = RequestMethod.POST)
    public String handleForm(@RequestBody String data,String SN,HttpServletRequest  req){
        System.out.println("设备上传的实时记录为..."+data+"序列号。。。。。。。"+SN);
        //若上传的实时记录类型为biophoto 若需要解析base64的比对照片 请参照ConvertBase642Img 类中的方法。
//        System.out.println("#######post,cdata请求的URL:"+req.getServletPath());
        Map<String,String> param = convertMap(req);
//        System.out.println("######post,cdata请求的参数"+param.toString());
//        System.out.println("######post,cdata请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        return "OK";
    }


    @RequestMapping(value = "/querydata",method = RequestMethod.POST)
    public String query(@RequestBody String querrydata){
        String[] dataArray = querrydata.split("\r\n");
        int count = dataArray.length;
        String tableName = dataArray[0].split(" ")[0];
        System.out.println("查询信息是。。。。。"+querrydata);
        String returnValue = tableName+"="+count;
        System.out.println(returnValue);
        return returnValue;

    }


    @RequestMapping(value="/devicecmd")
    public String deviceCmd(@RequestBody String cmdResult,HttpServletRequest req){
        System.out.println("命令返回的结果为..."+cmdResult);
//        System.out.println("#######devicecmd请求的URL:"+req.getServletPath());
//        Map<String,String> param = convertMap(req);
//        System.out.println("######devicecmd请求的参数"+param.toString());
//        System.out.println("######devicecmd请求完整URL:"+req.getServletPath()+"?"+param.toString().trim().replace(", ", "&").replace("{", "").replace("}", ""));
        return "OK";
    }

}

