package com.jd.springgear.context;

import com.jd.springgear.beans.AbstractSpringGearProxyProcessor;
import com.jd.springgear.engine.annotation.SpringGearProxy;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Set;

/**
 * 类扫描及后续处理的一个 scanner.
 *
 * @author
 **/
public class SpringGearProxyClassPathScanner extends ClassPathBeanDefinitionScanner {

    private final AbstractSpringGearProxyProcessor beanDefinitionProcessor;

    public SpringGearProxyClassPathScanner(BeanDefinitionRegistry registry, AbstractSpringGearProxyProcessor beanDefinitionProcessor) {
        super(registry, false);
        // 使用注解 {@link SpringGearProxy} 的过滤保证可以找到需要代理的类
        this.addIncludeFilter(new AnnotationTypeFilter(SpringGearProxy.class));
        this.beanDefinitionProcessor = beanDefinitionProcessor;
    }


    /**
     * 复写方法，扫描包，获取接口，进行代理实现。
     *
     * @param basePackages
     * @return
     */
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        // 扫描包，找到需要定义的 bean
        Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
        if (holders.isEmpty()) {
            logger.warn("No Spring Gear Interface found in package: '" + Arrays.toString(basePackages) + "'. Please check your configuration.");
            return holders;
        }
        // 自动代理接口实现
        beanDefinitionProcessor.autoProxyInterface(holders);
        return holders;
    }

    /**
     * 必须是接口类型
     *
     * @param beanDefinition
     * @return true/false
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isInterface() && metadata.isIndependent();
    }
}
