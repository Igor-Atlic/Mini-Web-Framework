package framework.annotations;
import framework.request.enums.Beans;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Bean {
    Beans scope() default Beans.singleton;
}
