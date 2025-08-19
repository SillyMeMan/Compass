package net.vinh.compass.annotation;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Marks a method or class as dangerous to use.
 * <p>
 * Using this will trigger IDE warnings (via ApiStatus.Internal).
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.CONSTRUCTOR})
@ApiStatus.Internal
public @interface Dangerous {
	String value() default "This class is marked as dangerous and is recommended to be replaced by new code. Use this class at your own risk";
}
