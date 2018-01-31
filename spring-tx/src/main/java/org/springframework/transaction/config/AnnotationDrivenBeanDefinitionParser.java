/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.transaction.config;

import org.w3c.dom.Element;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * {@link org.springframework.beans.factory.xml.BeanDefinitionParser
 * BeanDefinitionParser} implementation that allows users to easily configure
 * all the infrastructure beans required to enable annotation-driven transaction
 * demarcation.
 *
 * <p>By default, all proxies are created as JDK proxies. This may cause some
 * problems if you are injecting objects as concrete classes rather than
 * interfaces. To overcome this restriction you can set the
 * '{@code proxy-target-class}' attribute to '{@code true}', which
 * will result in class-based proxies being created.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Chris Beams
 * @since 2.0
 */
class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {

	/**
	 * The bean name of the internally managed transaction advisor (mode="proxy").
	 * @deprecated as of Spring 3.1 in favor of
	 * {@link TransactionManagementConfigUtils#TRANSACTION_ADVISOR_BEAN_NAME}
	 */
	@Deprecated
	public static final String TRANSACTION_ADVISOR_BEAN_NAME =
			TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME;

	/**
	 * The bean name of the internally managed transaction aspect (mode="aspectj").
	 * @deprecated as of Spring 3.1 in favor of
	 * {@link TransactionManagementConfigUtils#TRANSACTION_ASPECT_BEAN_NAME}
	 */
	@Deprecated
	public static final String TRANSACTION_ASPECT_BEAN_NAME =
			TransactionManagementConfigUtils.TRANSACTION_ASPECT_BEAN_NAME;


	/**
	 * Parses the {@code <tx:annotation-driven/>} tag. Will
	 * {@link AopNamespaceUtils#registerAutoProxyCreatorIfNecessary register an AutoProxyCreator}
	 * with the container as necessary.
	 */	// 解析标签 <tx:annotation-driven/>成对应的 BeanDefinition
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String mode = element.getAttribute("mode");
		if ("aspectj".equals(mode)) {
			// mode="aspectj"
			registerTransactionAspect(element, parserContext);
		}
		else {
			// mode="proxy"
			AopAutoProxyConfigurer.configureAutoProxyCreator(element, parserContext);
		}
		return null;
	}

	private void registerTransactionAspect(Element element, ParserContext parserContext) {
		String txAspectBeanName = TransactionManagementConfigUtils.TRANSACTION_ASPECT_BEAN_NAME;
		String txAspectClassName = TransactionManagementConfigUtils.TRANSACTION_ASPECT_CLASS_NAME;
		if (!parserContext.getRegistry().containsBeanDefinition(txAspectBeanName)) {
			RootBeanDefinition def = new RootBeanDefinition();
			def.setBeanClassName(txAspectClassName);
			def.setFactoryMethodName("aspectOf");
			registerTransactionManager(element, def);
			parserContext.registerBeanComponent(new BeanComponentDefinition(def, txAspectBeanName));
		}
	}

	private static void registerTransactionManager(Element element, BeanDefinition def) {
		def.getPropertyValues().add("transactionManagerBeanName",
				TxNamespaceHandler.getTransactionManagerName(element));
	}


	/**
	 * Inner class to just introduce an AOP framework dependency when actually in proxy mode.
	 */
	private static class AopAutoProxyConfigurer {
		//这个方法可以让spring 为事务自动创建代理,比较重要的是element中要指定对应的事务管理器
		public static void configureAutoProxyCreator(Element element, ParserContext parserContext) {
			AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);

			String txAdvisorBeanName = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME;		//事务bean的默认名称就叫这个名字
			if (!parserContext.getRegistry().containsBeanDefinition(txAdvisorBeanName)) {	//没有代表还没注册,需要注册进去
				Object eleSource = parserContext.extractSource(element);	//解析xml element

				// Create the TransactionAttributeSource definition.		//创建TransactionAttributeSource RootBeanDefinition
				RootBeanDefinition sourceDef = new RootBeanDefinition(
						"org.springframework.transaction.annotation.AnnotationTransactionAttributeSource");
				sourceDef.setSource(eleSource);
				sourceDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				// registerWithGeneratedName方法和registerBeanDefinition是一样的
				// 注册RootBeanDefinition到一个集中的map中,后续创建事务代理对象时需要用到这些definition
				// 为事务创建代理对象时,是先创建一个动态代理对象,然后把拦截器advisor都织入进去
				// 需要一个BeanPostProcesser,这个是哪里的呢?
				String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);

				// Create the TransactionInterceptor definition.			//创建TransactionInterceptor RootBeanDefinition
				RootBeanDefinition interceptorDef = new RootBeanDefinition(TransactionInterceptor.class);
				interceptorDef.setSource(eleSource);
				interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				registerTransactionManager(element, interceptorDef);		//注册事务管理器
				interceptorDef.getPropertyValues().add("transactionAttributeSource", new RuntimeBeanReference(sourceName));
				String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);

				// Create the TransactionAttributeSourceAdvisor definition.	//创建TransactionAttributeSourceAdvisor RootBeanDefinition
				RootBeanDefinition advisorDef = new RootBeanDefinition(BeanFactoryTransactionAttributeSourceAdvisor.class);	//定义ProxyBeanFactory getObject 为 BeanFactoryTransactionAttributeSourceAdvisor
				advisorDef.setSource(eleSource);
				advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				advisorDef.getPropertyValues().add("transactionAttributeSource", new RuntimeBeanReference(sourceName));
				advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);
				if (element.hasAttribute("order")) {
					advisorDef.getPropertyValues().add("order", element.getAttribute("order"));		//这种很多属性的,直接利用map塞值还是比较方便的
				}
				parserContext.getRegistry().registerBeanDefinition(txAdvisorBeanName, advisorDef);	//注册RootBeanDefinition

				CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
				compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));			//AnnotationTransactionAttributeSource
				compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));	//TransactionInterceptor
				compositeDef.addNestedComponent(new BeanComponentDefinition(advisorDef, txAdvisorBeanName));	//RootBeanDefinition BeanFactoryTransactionAttributeSourceAdvisor
				parserContext.registerComponent(compositeDef);	// 也是一个注册,好像是注册上面三者之间的关系??
			}
		}
	}

}
