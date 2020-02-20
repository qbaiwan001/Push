package com.zkteco.attpush.acc;

import java.io.*;
import java.util.Base64;

public class ConvertBase642Img {
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("d://lz.txt")));
        String str = bufferedReader.readLine();
        byte[] imgbyte = Base64.getDecoder().decode(str);
        OutputStream os = new FileOutputStream("d://lz.jpg");
        os.write(imgbyte);
        System.out.println(str);
    }
}
