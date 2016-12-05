package net.alantea.horizon.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //can use in method only.
@Repeatable(Listens.class)
public @interface Listen
{
   public String message() default "";
   public String[] messages() default {};
}
