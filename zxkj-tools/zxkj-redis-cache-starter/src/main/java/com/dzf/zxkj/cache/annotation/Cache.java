package com.dzf.zxkj.cache.annotation;

import com.dzf.zxkj.cache.constants.CacheScope;
import com.dzf.zxkj.cache.parser.ICacheResultParser;
import com.dzf.zxkj.cache.parser.IKeyGenerator;
import com.dzf.zxkj.cache.parser.impl.DefaultKeyGenerator;
import com.dzf.zxkj.cache.parser.impl.DefaultResultParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启缓存
 * <p/>
 * 解决问题：
 *
 * @author Ace
 * @version 1.0
 * @date 2017年5月4日
 * @since 1.7
 */
@Retention(RetentionPolicy.RUNTIME)
// 在运行时可以获取
@Target(value = {ElementType.METHOD, ElementType.TYPE})
// 作用到类，方法，接口上等
public @interface Cache {
    /**
     * 缓存key menu_{0.id}{1}_type
     *
     * @return
     * @author Ace
     * @date 2017年5月3日
     */
    String key() default "";

    /**
     * 作用域
     *
     * @return
     * @author Ace
     * @date 2017年5月3日
     */
    CacheScope scope() default CacheScope.application;

    /**
     * 过期时间
     *
     * @return
     * @author Ace
     * @date 2017年5月3日
     */
    int expire() default 720;

    /**
     * 描述
     *
     * @return
     * @author Ace
     * @date 2017年5月3日
     */
    String desc() default "";

    /**
     * 返回类型
     *
     * @return
     * @author Ace
     * @date 2017年5月4日
     */
    Class[] result() default Object.class;

    /**
     * 返回结果解析类
     *
     * @return
     */
    Class<? extends ICacheResultParser> parser() default DefaultResultParser.class;

    /**
     * 键值解析类
     *
     * @return
     */
    Class<? extends IKeyGenerator> generator() default DefaultKeyGenerator.class;
}
