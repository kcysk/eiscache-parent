
***
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## 说明
EisCache 是一个Java缓存抽象，依赖spring，提供了redis实现。

功能说明
* 自定义缓存失效时间 
* 根据实体类ID缓存key，保证更新数据的同时可以更新缓存的数据
* 接口式注解声明支持
* Spring Expression Language 支持

要求：
* JDK 1.6+
* Spring 4.0+ （spring3.0版本单独拉了一个分支）

## 使用
### method Cache
在接口或者实现方法上使用 `Cacheable` 注解即可

基类上使用
```java
@CacheDefault("#getFirstGenericType(#root.targetClass).newInstance().fetchCacheEntitName()")
public interface BaseService<T extends BaseEntity, K extends java.io.Serializable>{
    
    @Cacheable(entityId="#root.args[0]")
    T findOne(K id);
    
    @CacheRemove(entityId="#root.args[0]")
    void updateOne(T t);
}
```

普通接口使用
```java
@CacheDefault(cacheName="'user'") //若继承baseService则子类可不用次注解
public interface UserService extends BaseService<User, String>{
    @Cacheable(entityId="#root.args[0]", expire=100, timeUnit=java.util.concurrent.TimeUnit.HOURS)
    List<User> findUsersByIds(String[] ids);
    //remove
    @CacheRemove(entityId="#root.args[0].![#this.id]", cacheName="user") //可在此声明cacheName
    void updateUsers(List<User> users);
}
```


### 配置
```java
@org.springframework.context.annotation.Configuration
@EnableCache(advice=Advice.PROXY) 
public class EisCacheConfig { 
    
    @org.springframework.context.annotation.Bean
    public RedisTemplate redisTemplate(...){...}
    
    @org.springframework.context.annotation.Bean
    public TypeDescriptor typeDescriptor(...){...}
    
    @org.springframework.context.annotation.Bean
    public CacheManager cacheManager(...){...}
    
    @Bean(name = "dynamicCacheClassFilter")
    public DynamicCacheClassFilter classFilter(...){...}
}
```