package com.jd.springgear.extend.orika;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.ObjectFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * orika 的映射工具类，用于初始化及注册扫描到的映射等。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-17
 **/
public class OrikaBeanMapper extends ConfigurableMapper implements ApplicationContextAware, InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrikaBeanMapper.class);

    private ApplicationContext applicationContext;

    private MapperFactory factory;
    private MapperFacade facade;

    public OrikaBeanMapper() {
        super(false);
    }

    /**
     * 注册所有 converter.
     */
    private void registerConverters() {
        Map<String, Converter> converters = this.applicationContext.getBeansOfType(Converter.class);
        Converter converter;
        for (Map.Entry<String, Converter> kv : converters.entrySet()) {
            converter = kv.getValue();
            LOGGER.info("register converter bean '{}' with class '{}'", kv.getKey(), converter.getClass().getName());
            this.factory.getConverterFactory().registerConverter(converter);
        }
    }

    /**
     * 注册所有 Mapper。
     */
    private void registerMappers() {
        Map<String, Mapper> mappers = this.applicationContext.getBeansOfType(Mapper.class);
        Mapper mapper;
        for (Map.Entry<String, Mapper> kv : mappers.entrySet()) {
            mapper = kv.getValue();
            LOGGER.info("register mapper bean '{}' with class '{}'", kv.getKey(), mapper.getClass().getName());
            ClassMapBuilder builder = this.factory.classMap(mapper.getAType(), mapper.getBType());
            //  如果是 AdvancedCustomMapper，做独特逻辑处理。
            if (OrikaCustomMapper.class.isAssignableFrom(mapper.getClass())) {
                ((OrikaCustomMapper) mapper).beforeRegister(builder);
            }
            builder.byDefault().customize(mapper).register();
        }
    }


    /**
     * 注册对象工厂。
     */
    private void registerObjectFactories() {
        Map<String, ObjectFactory> objectFactories = this.applicationContext.getBeansOfType(ObjectFactory.class);
        ObjectFactory objectFactory;
        for (Map.Entry<String, ObjectFactory> kv : objectFactories.entrySet()) {
            objectFactory = kv.getValue();
            LOGGER.info("register Object Factory bean '{}' with class '{}'", kv.getKey(), objectFactory.getClass().getName());
        }
    }


    @Override
    protected void configure(MapperFactory factory) {
        this.factory = factory;
        this.registerConverters();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        this.init(); // 调用父类初始化方法，设置 factory 值。
        this.registerMappers();
        this.registerObjectFactories();
    }

}
