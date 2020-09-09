package com.chat.server;


import com.chat.server.config.AppConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Log4j2
public class ServerApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Autowired
    private AppConfiguration appConfiguration ;

    @Value("${server.port}")
    private int httpPort ;

    @Override
    public void run(String... args) throws Exception {
//        String addr = InetAddress.getLocalHost().getHostAddress();
//        Thread s = new Thread(new RegistryZK(addr,appConfiguration.getChatServerPort(),httpPort));
//        s.setName("registry-zk");
//        s.start();
//        log.info("项目启动====================================");
    }
}
