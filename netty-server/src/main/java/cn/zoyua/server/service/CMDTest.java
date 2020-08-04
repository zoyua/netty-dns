package cn.zoyua.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CMDTest {
    private static Logger logger = LoggerFactory.getLogger(CMDTest.class);

    public static boolean runMyCMD(String cmd) {
        try {
            System.out.println(cmd);
            Process p = Runtime.getRuntime().exec(cmd);
            Thread t1 = new Thread(new CMDResult(p.getInputStream()));
            Thread t2 = new Thread(new CMDResult(p.getErrorStream()));
            t1.start();
            t2.start();
            p.waitFor();
            p.getOutputStream().close();
            boolean rc = p.exitValue() == 0;
            p.destroy();
            return rc;
        } catch (Exception e) {
            logger.info("command execute error !");
            e.printStackTrace();
        }
        return false;
    }


    static class CMDResult implements Runnable {
        InputStream ins;

        public CMDResult(InputStream ins) {
            this.ins = ins;
        }

        //		@Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}