package net.zdsoft.cache.integration.spring;

import net.zdsoft.cache.annotation.EnableCache;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author shenke
 * @since 2017.09.28
 */
public abstract class AdviceImportSelector<A extends Annotation> implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Class<?> annoType = GenericTypeResolver.resolveTypeArgument(getClass(), AdviceImportSelector.class);
        AnnotationAttributes attributes = null;
        Map<String, Object> map = importingClassMetadata.getAnnotationAttributes(annoType.getName(), false);
        if ( map == null) {
            //
        }
        else if ( map instanceof AnnotationAttributes ) {
            attributes = (AnnotationAttributes) map;
        }
        else {
            attributes = new AnnotationAttributes(map);
        }
        if (attributes == null) {
            throw new IllegalArgumentException(String.format(
                    "@%s is not present on importing class '%s' as expected",
                    annoType.getSimpleName(), importingClassMetadata.getClassName()));
        }

        Advice advice = attributes.getEnum("advice");
        String[] imports = selectImports(advice);
        if (imports == null) {
            throw new IllegalArgumentException(String.format("Unknown AdviceMode: '%s'", advice));
        }
        return imports;
    }

    protected abstract String[] selectImports(Advice advice);

}
