package net.zdsoft.cache;

/**
 * @author shenke
 * @since 2017.09.06
 */
public interface Invoker {

    Object invoke() ;

    class ThrowableWrapper extends RuntimeException {
        private Throwable origin;

        public ThrowableWrapper(Throwable origin) {
            super(origin);
            this.origin = origin;
        }

        public Throwable getOrigin() {
            return origin;
        }
    }
}
