package com.jd.springgear.extend.jimdb.config;

import com.jd.jim.cli.Cluster;
import com.jd.jim.cli.ReloadableJimClientFactory;
import com.jd.jim.cli.config.ConfigLongPollingClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/15
 **/
@PropertySource(value = {
        "classpath:properties/cache.jimdb.properties", "classpath:cache.jimdb.properties"
}, ignoreResourceNotFound = true)
public class CacheJimDb {

    @Value("${cache.jimdb.endpoint}")
    private String endpoint;

    @Value("${cache.jimdb.url}")
    private String url;

    @Value("${cache.jimdb.timeout:60}")
    private int timeout;

    @Primary
    @Bean
    public Cluster jimDbClient() {

        return this.getClient(endpoint, url);
    }

    private Cluster getClient(String endpoint, String url) {
        // config factory
        ConfigLongPollingClientFactory configFactory = new ConfigLongPollingClientFactory();
        configFactory.setServiceEndpoint(endpoint);
        //http连接的建连超时时间和请求超时时间(单位是毫秒),不配置的话走默认5分钟->用来拉取集群的元数据信息和客户端的配置信息
        configFactory.setServiceTimeout((int) TimeUnit.SECONDS.toMillis(timeout));

        // client
        ReloadableJimClientFactory jimClient = new ReloadableJimClientFactory();
        this.process(jimClient);
        jimClient.setConfigClient(configFactory.create());
        jimClient.setJimUrl(url);
        // config id 配置        https://cf.jd.com/pages/viewpage.action?pageId=190004686
//        jimClient.setConfigId("0");
        return jimClient.getClient();
    }

    protected void process(ReloadableJimClientFactory jimClient) {
        //  netty IO线程池数量，一般情况下设置为2效果最佳，针对吞吐要求高的情况，可以根据不同的客户端CPU配置和集群规模建议测试后进行调整
        jimClient.setIoThreadPoolSize(2);
        //  流量控制，该队列由请求和响应两部组成，当队列瞬时达到1000，此时会提示超出队列长度，可以根据业务的流量进行调整，特别针对异步和pipeline调用
        jimClient.setRequestQueueSize(10000);
    }

}
