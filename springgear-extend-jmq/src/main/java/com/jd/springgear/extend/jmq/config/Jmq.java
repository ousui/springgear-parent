package com.jd.springgear.extend.jmq.config;

import com.jd.jmq.client.connection.ClusterTransportManager;
import com.jd.jmq.client.connection.TransportConfig;
import com.jd.jmq.client.connection.TransportManager;
import com.jd.jmq.client.consumer.Consumer;
import com.jd.jmq.client.consumer.ConsumerConfig;
import com.jd.jmq.client.consumer.MessageConsumer;
import com.jd.jmq.client.consumer.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/06/15
 **/
@PropertySource(value = {
        "classpath:jmq.properties", "classpath:properties/jmq.properties",
}, ignoreResourceNotFound = true)
@Slf4j
public class Jmq {

    @Value("${jmq.host}")
    private String host;

    @Value("${jmq.app}")
    private String app;

    @Value("${jmq.user}")
    private String user;

    @Value("${jmq.password}")
    private String password;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private Environment env;

    /**
     * topic 前缀
     */
    private static final String PROPERTY_JMQ_TOPIC_PREFIX = "jmq.topic.";


    @Bean(initMethod = "start", destroyMethod = "stop")
    public TransportManager jmqTransportManager() {
        //连接配置
        TransportConfig config = new TransportConfig();
        config.setApp(this.app);
        //设置broker地址
        config.setAddress(this.host);
        //设置用户名
        config.setUser(this.user);
        //设置密码
        config.setPassword(this.password);
        //创建集群连接管理器
        return new ClusterTransportManager(config);
    }


    @Bean(destroyMethod = "stop")
//    @DependsOn("jmqTransportManager") // start, stop 依赖
    public Consumer consumer(TransportManager jmqTransportManager) throws Exception {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        // 配置消费策略

        MessageConsumer consumer = new MessageConsumer(consumerConfig, jmqTransportManager, null);
        consumer.start();
        // jmq2 需要启动以后才可以订阅
        this.subscribe(consumer);
        return consumer;
    }

    private void subscribe(Consumer consumer) {
        Map<String, ? extends MessageListener> listeners = applicationContext.getBeansOfType(MessageListener.class);
//        Map<String, ? extends AbstractMessageListener> listeners = applicationContext.getBeansOfType(AbstractMessageListener.class);
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }

        listeners.entrySet().forEach(kv -> {
            String topic = env.getProperty(PROPERTY_JMQ_TOPIC_PREFIX.concat(kv.getKey()));
            String name = kv.getValue().getClass().getName();
            if (StringUtils.hasText(topic)) {
                log.debug("AbstractMessageListener 实现类 `{}` 的关联 bean `{}` 绑定 topic: {}", name, kv.getKey(), topic);
                consumer.subscribe(topic, kv.getValue());
            } else {
                log.warn("AbstractMessageListener 实现类 `{}` 的关联 bean `{}` 未定义 topic, 请按照格式配置配置文件：{}{BEAN_NAME}", name, kv.getKey(), PROPERTY_JMQ_TOPIC_PREFIX);
            }
        });

    }

}
