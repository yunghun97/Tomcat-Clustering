package com.example.tomcatsessionclustering2;

import org.apache.catalina.Context;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.example"})
public class TomcatSessionClustering2Application {

    public static void main(String[] args) {
        SpringApplication.run(TomcatSessionClustering2Application.class, args);
    }

    private static boolean distributable;

    public static boolean getDistributable(){
        return distributable;
    }
    @Bean
    public ServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                TomcatSessionClustering2Application.distributable = context.getDistributable();
                System.out.println("distributable is :"+distributable);
            }
        };
    }
}
