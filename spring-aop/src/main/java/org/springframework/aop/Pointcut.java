/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop;

/**
 * Core Spring pointcut abstraction.
 * <p/>
 * <p>A pointcut is composed of a {@link ClassFilter} and a {@link MethodMatcher}.
 * Both these basic terms and a Pointcut itself can be combined to build up combinations
 * (e.g. through {@link org.springframework.aop.support.ComposablePointcut}).
 *
 * @author Rod Johnson
 * @see ClassFilter
 * @see MethodMatcher
 * @see org.springframework.aop.support.Pointcuts
 * @see org.springframework.aop.support.ClassFilters
 * @see org.springframework.aop.support.MethodMatchers
 */
public interface Pointcut {
    //Pointcut由ClassFilter和MethodMatcher构成。
    //它通过ClassFilter定位到某些特定类上，通过MethodMatcher定位到某些特定方法上，这样Pointcut就拥有了描述某些类的某些特定方法的能力

	/*
     * 在介绍Pointcut之前，有必要先介绍  Join  Point（连接点）概念。
     * 连接点：程序运行中的某个阶段点，比如方法的调用、异常的抛出等。比如方法doSome();
     * Pointcut是JoinPoint的集合，它是程序中需要注入Advice 的位置的集合，指明Advice要在什么样的条件下才能被触发
	 */

    /**
     * Return the ClassFilter for this pointcut.
     *
     * @return the ClassFilter (never {@code null})
     */
    ClassFilter getClassFilter();

    /**
     * Return the MethodMatcher for this pointcut.
     *
     * @return the MethodMatcher (never {@code null})
     */
    MethodMatcher getMethodMatcher();


    /**
     * Canonical Pointcut instance that always matches.
     */
    Pointcut TRUE = TruePointcut.INSTANCE;

}
