/*<
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
 * A {@code TargetSource} is used to obtain the current "target" of			// TargetSource用来获取aop的目标对象
 * an AOP invocation, which will be invoked via reflection if no around		// 会通过反射调用
 * advice chooses to end the interceptor chain itself.						// 如果没有around拦截终止他
 *
 * <p>If a {@code TargetSource} is "static", it will always return			// 如果TargetSource是静态的,每次都会返回同一个对象.
 * the same target, allowing optimizations in the AOP framework. Dynamic	// 允许修改
 * target sources can support pooling, hot swapping, etc.					// 动态修改target sources可以支持池化和热切换
 *
 * <p>Application developers don't usually need to work with				// 应用开发者正常不需要关心
 * {@code TargetSources} directly: this is an AOP framework interface.		// 这是Spring aop框架中的接口
 *
 * @author Rod Johnson
 */
public interface TargetSource extends TargetClassAware {

	/**
	 * Return the type of targets returned by this {@link TargetSource}.
	 * <p>Can return {@code null}, although certain usages of a
	 * {@code TargetSource} might just work with a predetermined
	 * target class.
	 * @return the type of targets returned by this {@link TargetSource}
	 */
	Class<?> getTargetClass();

	/**
	 * Will all calls to {@link #getTarget()} return the same object?
	 * <p>In that case, there will be no need to invoke
	 * {@link #releaseTarget(Object)}, and the AOP framework can cache
	 * the return value of {@link #getTarget()}.
	 * @return {@code true} if the target is immutable
	 * @see #getTarget
	 */
	boolean isStatic();

	/**
	 * Return a target instance. Invoked immediately before the
	 * AOP framework calls the "target" of an AOP method invocation.
	 * @return the target object, which contains the joinpoint
	 * @throws Exception if the target object can't be resolved
	 */
	Object getTarget() throws Exception;

	/**
	 * Release the given target object obtained from the
	 * {@link #getTarget()} method.
	 * @param target object obtained from a call to {@link #getTarget()}
	 * @throws Exception if the object can't be released
	 */
	void releaseTarget(Object target) throws Exception;

}
