package net.zdsoft.cache.aspectj;

/**
 * @author shenke
 * @since 2017.08.30
 */
public class ThrowAny {

    static void throwUnchecked(Throwable e) {
        ThrowAny.<RuntimeException>throwAny(e);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAny(Throwable e) throws E {
        throw (E) e;
    }
}
