package net.alantea.horizon.message;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface MessageControler
{
   /**
    * Value.
    *
    * @return the string
    */
   String value() default "";
}
