package net.zdsoft.cache;

/**
 * @author shenke
 * @since 2017.10.11
 */
public class Constant {

    public static final String BEAN_NAME_ADVISOR           = "org.zdsoft.cache.proxy.internalCacheAdvisor";

    public static final String ASPECTJ_CONFIGURATION_CLASS = "net.zdsoft.cache.aop.aspectj.AspectjCacheConfiguration";

    public static final String PROXY_CONFIGURATION_CLASS   = "net.zdsoft.cache.aop.proxy.CacheProxyConfiguration";


    public static final String SLOW_CACHE_NAME   = "eis-cache.slowCache";
    public static final String SLOW_INVOKE_NAME  = "eis-cache.slowInvoke";

    public static final long DEFAULT_SLOW_CACHE  = 100L;
    public static final long DEFAULT_SLOW_INVOKE = 100L;
}
