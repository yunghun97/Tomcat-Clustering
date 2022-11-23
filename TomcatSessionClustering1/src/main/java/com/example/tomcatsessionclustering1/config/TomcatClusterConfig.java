package com.example.tomcatsessionclustering1.config;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.ha.session.ClusterSessionListener;
import org.apache.catalina.ha.session.DeltaManager;
import org.apache.catalina.ha.session.JvmRouteBinderValve;
import org.apache.catalina.ha.tcp.ReplicationValve;
import org.apache.catalina.ha.tcp.SimpleTcpCluster;
import org.apache.catalina.tribes.group.GroupChannel;
import org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor;
import org.apache.catalina.tribes.group.interceptors.TcpFailureDetector;
import org.apache.catalina.tribes.group.interceptors.TcpPingInterceptor;
import org.apache.catalina.tribes.membership.McastService;
import org.apache.catalina.tribes.transport.ReplicationTransmitter;
import org.apache.catalina.tribes.transport.nio.NioReceiver;
import org.apache.catalina.tribes.transport.nio.PooledParallelSender;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatClusterConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(final TomcatServletWebServerFactory factory) {
        factory.addContextCustomizers(new TomcatClusterContextCustomizer());
    }
}
class TomcatClusterContextCustomizer implements TomcatContextCustomizer {
    @Override
    public void customize(final Context context) {
        context.setDistributable(true);

        DeltaManager manager = new DeltaManager();
        manager.setExpireSessionsOnShutdown(false);
        manager.setNotifyListenersOnReplication(true);
        context.setManager(manager);
        configureCluster((Engine) context.getParent().getParent());
    }
    private void configureCluster(Engine engine) {
        //cluster
        SimpleTcpCluster cluster = new SimpleTcpCluster();
        cluster.setChannelSendOptions(6);

        //channel
        GroupChannel channel = new GroupChannel();

        //membership setting
        McastService mcastService = new McastService();
        mcastService.setAddress("228.0.0.4");
        mcastService.setPort(45564); // TCP&UDP port 오픈 필요
        mcastService.setFrequency(500);
        mcastService.setDropTime(3000);
        channel.setMembershipService(mcastService);

        //receiver
        NioReceiver receiver = new NioReceiver();
        receiver.setAddress("auto"); // 자신의 IP -> auto는 자신의 IP 들어가는 듯?
        receiver.setMaxThreads(6);  // 최대 스레드
        receiver.setPort(5000); // TCP port 오픈 필요 (수신 포트)
        channel.setChannelReceiver(receiver);

        //sender
        ReplicationTransmitter sender = new ReplicationTransmitter();
        sender.setTransport(new PooledParallelSender());
        channel.setChannelSender(sender);

        //interceptor
        channel.addInterceptor(new TcpPingInterceptor());
        channel.addInterceptor(new TcpFailureDetector());
        channel.addInterceptor(new MessageDispatchInterceptor());
        cluster.addValve(new ReplicationValve());
        cluster.addValve(new JvmRouteBinderValve());
        cluster.setChannel(channel);
        cluster.addClusterListener(new ClusterSessionListener());
        engine.setCluster(cluster);
    }
}