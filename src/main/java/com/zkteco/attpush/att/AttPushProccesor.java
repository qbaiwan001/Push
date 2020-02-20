package com.zkteco.attpush.att;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.codec.binary.Base64;

/**
 * 所有的设备请求都会在url参数里携带SN，这是设备序列号(serial number的缩写)，每个设备唯一标识
 */
//@Controller
@RequestMapping("/iclock")//http://ip:port/iclock/cdata?options=all&lanuage=83&pushver=2.2
public class AttPushProccesor {

    private static Map<String, List<String>> cmdMap = new HashMap<>();
    /**
     * 1,设备通完电以后第一个发送到后台的请求
     * 格式为 /iclock/cdata?options=all&language=xxxx&pushver=xxxx
     */
    @RequestMapping(value="/cdata",params = {"options","language","pushver"},method = RequestMethod.GET)
    public void init(String SN, String options, String language, String pushver, @RequestParam(required = false) String PushOptionsFlag, HttpServletResponse response){
        System.out.println("options="+options+"....language="+language+"....pushver="+pushver);
        try {
            System.out.println("考勤机初始化请求进来了......"+PushOptionsFlag);
            loadCmd(SN);//加载命令
            String initOptions = initOptions(SN,PushOptionsFlag);
            System.out.println("返回给考勤机的初始化参数为...."+initOptions);
            response.getWriter().write(initOptions);//返回成功以后设备会发送心跳请求
            //response.getWriter().write("UNKNOWN DEVICE");当返回这个的时候，设备会每2秒重新发本请求，直到返回OK为止
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2，心跳请求，会从服务器拿到命令返回给设备
     */
    @RequestMapping("/getrequest")
    public void heartBeat(String SN, HttpServletResponse response, HttpServletRequest request,String INFO){
//    	System.out.println("路径："+request.getServletPath());
//    	System.out.println("路径222："+request.getScheme());
//    	System.out.println("路径333："+request.getServerName());
//    	System.out.println("路径444："+request.getLocalName());
        System.out.println("SN号："+request.getParameter("SN"));
//        System.out.println("INFO信息："+INFO);
       StringBuffer sb = new StringBuffer("OK");
       List<String> cmds =  cmdMap.get(SN);
        if(cmds==null){//等于空说明从来没加载过，如果初始化加载过了，此时应该不为Null 只是size为0
            loadCmd(SN);
            cmds = cmdMap.get(SN);
        }
       if(cmds!=null&&cmds.size()>0){
           sb.setLength(0);//如果有命令就不返回OK了
          cmds.stream().forEach(cmd->sb.append(cmd).append("\r\n\r\n"));

       }
       System.out.println("心跳命令为...."+sb);
       //System.out.println("此心跳时间："+new Date());
        try {
            cmdMap.get(SN).clear();//处理完以后立刻将集合清空，实际开发中应该是在/devicecmd这个请求里完成
			response.setCharacterEncoding("gbk");
            response.getWriter().write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 3，候补心跳请求，正常情况下设备不发此请求，有大量数据上传的时候，不发上面的心跳，发这个请求，
     * 这个请求，服务器只能返回OK，不可以返回命令
     */
    @RequestMapping("/ping")
    public void ping(HttpServletResponse response){
        System.out.println("考勤机心跳请求/ping："+new SimpleDateFormat("HH:mm:ss").format(new Date()));
        try {
            response.getWriter().write("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 4，设备端处理完命令以后会发送该请求，告诉服务器命令的处理结果
     */
    @RequestMapping(value="/devicecmd",method = RequestMethod.POST)
    public void handleCmd(String sn,@RequestBody  String data,HttpServletResponse response){
        System.out.println("设备处理完命令以后的返回结果..."+data);
        try {
            response.getWriter().write("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping("/fdata")
    public String handleFdata(){
        System.out.println("进入fdata........");
        return "OK";
    }

    @RequestMapping(value = "/cdata")
    public void handleRtData(HttpServletRequest request,HttpServletResponse response,String SN,String table,
    		String Stamp){
        String data = "";
        System.out.println("table名字为:"+table);
//        System.out.println("Stamp名字为:"+Stamp);
        ByteArrayOutputStream bos = null;
        byte[] b= new byte[1024];
        try {
            InputStream is = request.getInputStream();
            bos = new ByteArrayOutputStream();
            int len = 0;
            while((len=is.read(b))!=-1){
                bos.write(b,0,len);
            }
            data = new String(bos.toByteArray(),"gbk");	//将流信息转化成字符串并解决乱码
        } catch (IOException e) {
            e.printStackTrace();
        }
         System.out.println("设备的实时记录是......."+data);

        if("ATTPHOTO".equals(table)){
            //如果是考勤图片上传,此处重点演示如何处理上传到软件里的照片
            try {
                String photo = getAttPhotoDataInfo(data,bos.toByteArray()).getString("static/photo");
                saveFile(System.currentTimeMillis()+".jpg",photo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if("OPERLOG".equals(table)){
            if(data.startsWith("BIOPHOTO")){//处理人员上传上拉的比对照片模板
                Map<String,String> preInfoMap = formateStrToMap(data,"\t");
                System.out.println("preInfoMap..."+preInfoMap);
                //处理上传上来的比对照片
                saveFile(System.currentTimeMillis()+".jpg",preInfoMap.get("Content"));
            }
        }
        try {
            response.getWriter().write("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCmd(String sn){
        try{
            List<String> cmdList = new ArrayList<>();
            File file = ResourceUtils.getFile("classpath:cmd.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String cmd = "";
            while((cmd = br.readLine())!=null){
                if(!cmd.startsWith("#")){
                    System.out.println("加载进内存的命令是...."+cmd);
                    cmdList.add(cmd);
                }
            }
            cmdMap.put(sn,cmdList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 设备通电以后连接到服务器，需要返回的初始化参数
     * @param sn
     * @param PushOptionsFlag
     * @return
     */
    private String initOptions(String sn,String PushOptionsFlag) {
        StringBuffer devOptions = new StringBuffer();
        devOptions.append("GET OPTION FROM: "+sn);
        // + "\nStamp=" + devInfo.devInfoJson.getString("Stamp")
        devOptions.append("\nATTLOGStamp=0");
        devOptions.append("\nOPERLOGStamp=0");
        devOptions.append("\nBIODATAStamp=0");
        devOptions.append("\nATTPHOTOStamp=0");
        devOptions.append("\nErrorDelay=3");//断网重连
        devOptions.append("\nDelay=3");//心跳间隔
        devOptions.append("\nTimeZone=8");//时区
        devOptions.append("\nRealtime=1");//实时上传
        devOptions.append("\nServerVer=3.0.1");//这个必须填写
        devOptions.append("\nPushProtVer=2.4.1");
//        1 考勤记录
//        2 操作日志
//        3 考勤照片
//        4 登记新指纹
//        5 登记新用户
//        6 指纹图片
//        7 修改用户信息
//        8 修改指纹
//        9 新登记人脸
//        10 用户照片
//        11 工作号码
//        12 比对照片
        devOptions.append("\nTransFlag=111111111111");//  1-12二进制位 分别代表以上含义
        System.out.println("PushOptionsFlag=========="+PushOptionsFlag);
        if (PushOptionsFlag!=null&&PushOptionsFlag.equals("1"))
        {
            // 支持参数单独获取的才要把需要获取的参数回传给设备 modifeid by max 20170926
            devOptions.append("\nPushOptions=RegDeviceType,FingerFunOn,FaceFunOn,FPVersion,FaceVersion,NetworkType,HardwareId3,HardwareId5,HardwareId56,LicenseStatus3,LicenseStatus5,LicenseStatus56");
        }
        devOptions.append("\n");
        return devOptions.toString();
    }

    private JSONObject getAttPhotoDataInfo(String data,byte[] bufArray) {
        if (StringUtils.isNotBlank(data)) {
            String[] headArray = data.split("CMD=uploadphoto",2);
            JSONObject jsonData = new JSONObject();
            Map<String,String> preInfoMap = formateStrToMap(headArray[0],"\n");
            for (String key:preInfoMap.keySet()) {
                setKeyValue(jsonData,key,preInfoMap.get(key));
            }
            int photoBufLength = Integer.valueOf(preInfoMap.get("size"));
            byte[] fileDataBytesBase64 = Base64.encodeBase64(ArrayUtils.subarray(bufArray, bufArray.length - photoBufLength, photoBufLength));
            String fileDataStrBase64 = new String(fileDataBytesBase64);
            setKeyValue(jsonData,"photo",fileDataStrBase64);
            setKeyValue(jsonData, "static/photo",fileDataStrBase64);
            return jsonData;
        } else {
            return null;
        }

    }
    

    public static Map<String, String> formateStrToMap(String data,String split)
    {
        Map<String, String> dataMap = new HashMap<String, String>();
        String[] dataArray = data.split(split);
        for (String d : dataArray)
        {
            String[] oneData = d.split("=",2);
            if (oneData.length==2)
            {
                dataMap.put(oneData[0], oneData[1].replace("\n",""));
            } else if(oneData.length==1) {
                dataMap.put(oneData[0], "");
            }
        }
        return dataMap;
    }

    private static void setKeyValue(JSONObject json, String key, Object value)
    {
        try
        {
            json.putOpt(key, value);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 保存base64图片
     *
     * @param fileName
     * @param base64ImgStr
     */
    private static void saveFile(String fileName, String base64ImgStr) {
        try {
            String fullPath = "D://photo";//如果没有D盘，需要修改一下图片存放位置
            File file = new File(new File(fullPath), fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            OutputStream out = new FileOutputStream(file);
            out.write(java.util.Base64.getDecoder().decode(base64ImgStr));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
