package cn.zoyua.client.controller;

import cn.zoyua.client.netty.DnsClient;
import cn.zoyua.client.netty.ClientHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dns/host")
public class HostsController {
    private static Logger logger = LoggerFactory.getLogger(HostsController.class);

    //数据都用json处理
    @RequestMapping(value = "/list")
    public String getHostsList() throws Exception {
        Channel ch = DnsClient.getCh();
        String tem = String.valueOf(System.currentTimeMillis());
        ClientHandler.setResponse(tem);
        ch.writeAndFlush("hostList" + "\r\n");
        while (tem.equals(ClientHandler.getResp())) {
            Thread.sleep(1);
        }
        return ClientHandler.getResp();
    }

    @GetMapping("/ip")
    public String searchIp() {
        System.out.println("hello get host list");
        //获取整张表，然后从表中查找
        return "1223344";
    }

    @GetMapping("/domain")
    public String searchDomain() {
        System.out.println("hello get host list");
        //获取整张表，然后从表中查找
        return "";
    }

    @PostMapping("/add")
    public String addHost() {
        //向list中add
        return "";
    }

    @RequestMapping("/update")
    public String update(@RequestParam("host") String host) throws Exception {
        logger.info("update host:{}", host);
        Channel ch = DnsClient.getCh();
        String tem = String.valueOf(System.currentTimeMillis());
        ClientHandler.setResponse(tem);
        ch.writeAndFlush("update_" + host + "\r\n");
        while (tem.equals(ClientHandler.getResp())) {
            Thread.sleep(1);
        }
        return ClientHandler.getResp();
    }

    @PostMapping("/delete")
    public String delete() {
        //获取整张表，查找到需要删除的然后删除
        return "";
    }

    /**
     * 获取时间戳，作为通信凭证
     *
     * @return
     */
    public String getRandom() {
        return String.valueOf(System.currentTimeMillis());
    }
}
