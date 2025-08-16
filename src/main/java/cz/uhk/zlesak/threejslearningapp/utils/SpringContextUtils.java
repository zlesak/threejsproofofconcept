package cz.uhk.zlesak.threejslearningapp.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * SpringContextUtils Class - Utility class to access Spring ApplicationContext and retrieve beans.
 * This class implements ApplicationContextAware to allow Spring to inject the application context.
 * Used mainly for i18n beans and other components that need to be accessed statically.
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}

