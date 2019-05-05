package net.alantea.horizon.message;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface MessageAction
{
   /**
    * Value.
    *
    * @return the string
    */
   String value() default "";
}
