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

import org.aopalliance.aop.Advice;

/**
 * Base interface holding AOP <b>advice</b> (action to take at a joinpoint)	//Advisor 切面 持有增强(advice)的引用
 * and a filter determining the applicability of the advice (such as
 * a pointcut). <i>This interface is not for use by Spring users, but to
 * allow for commonality in support for different types of advice.</i>
 *
 * <p>Spring AOP is based around <b>around advice</b> delivered via method	//Spring aop基于interception的前后增强(around advice)
 * <b>interception</b>, compliant with the AOP Alliance interception API.
 * The Advisor interface allows support for different types of advice,		//Advisor 支持不同类型的增强
 * such as <b>before</b> and <b>after</b> advice, which need not be			//比如前置和后置增强
 * implemented using interception.
 *
 * @author Rod Johnson
 */
public interface Advisor {	//-->Advisor 切面就是由切点(pointcut)和增强(advice)组成
	//因为Advice包含了横切代码和连接点的信息，所以Advice本身就是一个简单的切面，只不过它代表的横切的连接点是所有目标类的所有方法，因为这个横切面太宽泛，所以一般不会直接使用；
	//Advisor是Pointcut和Advice的配置器，它包括Pointcut和Advice，是将Advice注入程序中Pointcut位置的代码
	/**
	 * Return the advice part of this aspect. An advice may be an
	 * interceptor, a before advice, a throws advice, etc.
	 * @return the advice that should apply if the pointcut matches
	 * @see org.aopalliance.intercept.MethodInterceptor
	 * @see BeforeAdvice
	 * @see ThrowsAdvice
	 * @see AfterReturningAdvice
	 */
	Advice getAdvice();

	/**
	 * Return whether this advice is associated with a particular instance
	 * (for example, creating a mixin) or shared with all instances of
	 * the advised class obtained from the same Spring bean factory.
	 * <p><b>Note that this method is not currently used by the framework.</b>
	 * Typical Advisor implementations always return {@code true}.
	 * Use singleton/prototype bean definitions or appropriate programmatic
	 * proxy creation to ensure that Advisors have the correct lifecycle model.
	 * @return whether this advice is associated with a particular target instance
	 */
	boolean isPerInstance();

}
