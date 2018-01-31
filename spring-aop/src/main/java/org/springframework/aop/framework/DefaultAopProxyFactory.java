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

package org.springframework.aop.framework;

import org.springframework.aop.SpringProxy;

import java.io.Serializable;

/**
 * Default {@link AopProxyFactory} implementation,
 * creating either a CGLIB proxy or a JDK dynamic proxy.
 *
 * <p>Creates a CGLIB proxy if one the following is true
 * for a given {@link AdvisedSupport} instance:				//三个要求符合一个就会使用Cglib代理
 * <ul>
 * <li>the "optimize" flag is set
 * <li>the "proxyTargetClass" flag is set
 * <li>no proxy interfaces have been specified
 * </ul>
 *
 * <p>Note that the CGLIB library classes have to be present on
 * the class path if an actual CGLIB proxy needs to be created.			// 如果需要创建cglib代理,必须依赖cglib的jar包
 *
 * <p>In general, specify "proxyTargetClass" to enforce a CGLIB proxy,	// 一般的,指定proxyTargetClass为true,来强制使用cglib动态代理
 * or specify one or more interfaces to use a JDK dynamic proxy.		// 或者指定一个或者多个接口,使用jdk动态代理
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 12.03.2004
 * @see AdvisedSupport#setOptimize
 * @see AdvisedSupport#setProxyTargetClass
 * @see AdvisedSupport#setInterfaces
 */
@SuppressWarnings("serial")
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {


	public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {	// 创建Aop代理对象
		if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {	// isOptimize isProxyTargetClass都是配置选项
			Class targetClass = config.getTargetClass();														// isOptimize默认false,可以指定ProxyFactoryBean配置isOptimize为true
			if (targetClass == null) {													// 没有目标对象,抛异常
				throw new AopConfigException("TargetSource cannot determine target class: " +
						"Either an interface or a target is required for proxy creation.");
			}
			if (targetClass.isInterface()) {		// 即使开启了使用cglib代理, 如果targetClass是接口, 仍然使用Jdk动态代理
				return new JdkDynamicAopProxy(config);
			}
			return CglibProxyFactory.createCglibProxy(config);	// 不是接口才会使用Cglib代理
		}
		else {
			return new JdkDynamicAopProxy(config);
		}
	}

	/**
	 * Determine whether the supplied {@link AdvisedSupport} has only the
	 * {@link org.springframework.aop.SpringProxy} interface specified
	 * (or no proxy interfaces specified at all).
	 */
	private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
		Class[] interfaces = config.getProxiedInterfaces();
		return (interfaces.length == 0 || (interfaces.length == 1 && SpringProxy.class.equals(interfaces[0])));
	}


	/**
	 * Inner factory class used to just introduce a CGLIB dependency
	 * when actually creating a CGLIB proxy.
	 */
	private static class CglibProxyFactory {

		public static AopProxy createCglibProxy(AdvisedSupport advisedSupport) {
			return new CglibAopProxy(advisedSupport);
		}
	}

}
