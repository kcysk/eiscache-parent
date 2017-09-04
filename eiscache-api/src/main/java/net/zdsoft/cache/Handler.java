package net.zdsoft.cache;

import net.zdsoft.cache.core.CacheInvocationContext;

/**
 * @author shenke
 * @since 17-9-4下午9:54
 */
public interface Handler {

    Object invoke(Invoker invoker, CacheInvocationContext invocationContext);

    /**
     * @author shenke
     * @since 2017.09.04
     */
    interface Invoker {

        Object invoke() throws ThrowableWrapper;

        class ThrowableWrapper extends RuntimeException {
            private Throwable origin;

            public ThrowableWrapper(Throwable cause, Throwable origin) {
                super(cause);
                this.origin = origin;
            }
            public Throwable getOrigin() {
                return this.origin;
            }
        }
    }
}
