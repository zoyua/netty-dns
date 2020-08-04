package cn.zoyua.server.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hosts2Json {
    private static Logger logger = LoggerFactory.getLogger(Hosts2Json.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static Pattern p = Pattern.compile(".*\\d+.*");
    private static StringBuffer buffer = new StringBuffer(1024 * 15);
    private static String HOSTS_PATH = "/export/host";
    //host 备份路径
    private static String COPY_PATH = "/export/copy-host";

    /**
     *
     * 将host转成[{"134.45.23.11":"rhino.xxx.com"},{"2.4.4.7":"x.bai.com"}]这种格式
     * @param  "C:\\Users\\Administrator\\Desktop\\hosts"
     * @throws Exception
     */
    public static String changeHost2Str()throws Exception{
        File dir = new File(HOSTS_PATH);
        List<Map<String,String>> list = new ArrayList<Map<String, String>>();
        File[] fileArray = dir.listFiles();
        int len = fileArray.length;
        int index = 0;
        for (int i=0;i<len;i++){
            File f = fileArray[i];
            FileInputStream stream = new FileInputStream(f);
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(reader);
            String txt;
            while((txt=br.readLine())!=null){
                Matcher m = p.matcher(txt);
                if (m.matches()) {
                    String[] argu = txt.split("\\s+");
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("t", String.valueOf(index));
                    map.put("ip",argu[0]);
                    map.put("domain",argu[1]);
                    list.add(map);
                    index++;
                }
            }
            br.close();
            reader.close();
            stream.close();
        }
        if (buffer.length() > 0) {
            buffer.delete(0, buffer.length());
        }
        buffer.append(mapper.writeValueAsString(list));
        logger.info(buffer.toString());
        return buffer.toString();
    }

    /**
     * 这个只是解析json格式的host
     * @throws Exception
     */
    public static void str2host()throws Exception{

        String s = "[{\"ip\":\"102.54.94.97\",\"domain\":\"rhino.xxx.com\"},{\"ip\":\"38.25.63.10\",\"domain\":\"x.acme.com\"}]";
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String,String>> list = mapper.readValue(s,List.class);
        Iterator<Map<String,String>> it = list.iterator();
        while (it.hasNext()){
            Map<String,String> map = it.next();
            String ip = map.get("ip");
            String domain = map.get("domain");
            System.out.println(ip+" "+domain);
        }
    }

    /**
     * 将json格式的host整理写入对应的文件
     * @param hostJson
     * @throws Exception
     */
    public static boolean writeHosts(String hostJson) throws Exception {
        logger.info("writeHosts:" + hostJson);
        copyHostBeforeUpdate();
        cleanHostDir(HOSTS_PATH);
        Map<String,List<String>> hostList = new HashMap<String, List<String>>();
        List<Map<String,String>> list = mapper.readValue(hostJson,List.class);
        Iterator<Map<String,String>> it = list.iterator();
        while (it.hasNext()){
            Map<String,String> map = it.next();
            String ip= map.get("ip");
            String domain=map.get("domain");
            StringBuilder sb = new StringBuilder();
            sb.append(ip).append("  ").append(domain);
            logger.info(sb.toString());
            String secondDomain = getFileName(domain);
            if(hostList.get(secondDomain)!=null){
                hostList.get(secondDomain).add(sb.toString());
            }else {
                List<String> tem = new ArrayList<String>();
                tem.add(sb.toString());
                hostList.put(secondDomain,tem);
            }
        }
        Iterator<Map.Entry<String,List<String>>> hostIt = hostList.entrySet().iterator();
        while(hostIt.hasNext()){
            Map.Entry<String,List<String>> ent = hostIt.next();
            String dom = ent.getKey();
            List<String> ipmap = ent.getValue();
            File file = new File(HOSTS_PATH + "/" + dom + ".hosts");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            Iterator<String> iter = ipmap.iterator();
            while (iter.hasNext()){
                bw.write(iter.next());
                bw.newLine();
            }
            bw.close();
        }
        return true;
    }


    public static String getFileName(String domain){
        int index = domain.lastIndexOf('.');
        String substr = domain.substring(0,index);
        index = substr.lastIndexOf('.');
        String name = domain.substring(index+1);
        logger.info(name);
        return name;
    }

    /**
     * 每次写之前都清空host路径
     * @param dir
     */
    public static void cleanHostDir(String dir){
        File f = new File(dir);
        if (f.exists() && f.isDirectory()){
            File[] arr = f.listFiles();
            if (arr != null){
                int len = arr.length;
                if(len ==0){
                    return;
                }
                for(int i=0;i<len;i++){
                    if(!(arr[i].delete())){
                        System.out.println("delete file failed before update host");
                    }
                }
            }
        }
    }

    /**
     * 每次更新host文件时，先备份
     *
     * @since 1.7
     */
    public static void copyHostBeforeUpdate() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(new Date());
        System.out.println(date);
        File dir = new File(COPY_PATH + File.separator + date);
        dir.mkdir();
        String path = dir.getAbsolutePath();
        File hostdir = new File(HOSTS_PATH);
        File[] list = hostdir.listFiles();
        if (list != null && list.length > 0) {
            int num = list.length;
            for (int i = 0; i < num; i++) {
                Path source = Paths.get(list[i].getAbsolutePath());
                Path target = Paths.get(path + File.separator + list[i].getName());
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /**
     * 写文件
     * @param fileName host文件名
     * @param list 里面放的是一条一条的host
     * @throws Exception
     */
    public static void writeFile(String fileName, List<String> list) throws Exception {
        File fout = new File("out.txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (int i = 0; i < 10; i++) {
            bw.write("something");
            bw.newLine();
        }
        bw.close();
    }

    public void readHostsFile() throws Exception{
        /*File f = new File("C:\\Users\\Administrator\\Desktop\\hosts");
        FileInputStream stream = new FileInputStream(f);
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);
        String txt = null;
        while((txt=br.readLine())!=null){
            String[] argu = txt.split("/s{1,30}");
            System.out.println(argu);
        }*/
    }

}
