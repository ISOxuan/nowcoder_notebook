package com.nowcoder.community;

import java.io.IOException;

public class WKTests {
    public static void main(String[] args) {
        String cmd = "/usr/local/bin/wkhtmltopdf https://www.nowcoder.com /home/ztx/data/wk-pdfs/1.pdf";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
