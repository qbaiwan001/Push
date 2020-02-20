package com.zkteco.attpush.miniProgramme;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pos/api")
public class ProgrammeController {

    @RequestMapping("/myInfo")
    public ResultMsg getMyInfo(String token){
        System.out.println("token..."+token);
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setRet("success");
        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return resultMsg;
    }

    @RequestMapping("/posIDApiToken/getToken")
    public ResultMsg getToken(@RequestBody String params){
        System.out.println(params);
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setData("abcdefg");
        resultMsg.setRet("success");
        return resultMsg;
    }
}
