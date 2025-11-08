package io.github.ngspace.hudder.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Don't want Hudder to expose your deepest, darkest methods or fields to the user?<br>
 * WELL THEN JUST SLAP THIS GAL ONTO ALL YOUR IMPORTANT SHIT AND I WILL DO MY BEST TO KEEP YOUR SECRET SOMEWHAT SECURE.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NoAccess {}
