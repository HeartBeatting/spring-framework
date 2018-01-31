/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.beans;

import java.beans.PropertyDescriptor;

/**	Spring里面像这种包装类挺多的,但是都是有他的用途的,Spring的设计的思想可以借鉴下.
 *  像这种包装类,都是对被包装对象的增强,看下下面增强了哪些方法.
 * The central interface of Spring's low-level JavaBeans infrastructure.	//Spring低级别的基础设施
 *
 * <p>Typically not used directly but rather implicitly via a				//不会直接使用
 * {@link org.springframework.beans.factory.BeanFactory} or a				//一般通过BeanFactory
 * {@link org.springframework.validation.DataBinder}.
 *
 * <p>Provides operations to analyze and manipulate standard JavaBeans:		//提供了可以对JavaBeans进行分析和操纵的方法
 * the ability to get and set property values (individually or in bulk),	//可以单个或者批量获取,设置属性值
 * get property descriptors, and query the readability/writability of properties.	//获取属性描述, 和获取属性值的可读或可写性
 *
 * <p>This interface supports <b>nested properties</b> enabling the setting	//支持嵌套属性
 * of properties on subproperties to an unlimited depth.					//可以设置无限深度的子属性
 *
 * <p>A BeanWrapper's default for the "extractOldValueForEditor" setting	//这个值默认false
 * is "false", to avoid side effects caused by getter method invocations.	//为了避免get方法的副作用
 * Turn this to "true" to expose present property values to custom editors.	//修改它为true,将当前属性暴露给普通编辑者
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13 April 2001
 * @see PropertyAccessor
 * @see PropertyEditorRegistry
 * @see PropertyAccessorFactory#forBeanPropertyAccess
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.validation.BeanPropertyBindingResult
 * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
 */
public interface BeanWrapper extends ConfigurablePropertyAccessor {

	/**
	 * Return the bean instance wrapped by this object, if any.
	 * @return the bean instance, or {@code null} if none set	//返回包裹的bean实例
	 */
	Object getWrappedInstance();

	/**
	 * Return the type of the wrapped JavaBean object.			//返回包裹的JavaBean对象的class
	 * @return the type of the wrapped bean instance,
	 * or {@code null} if no wrapped object has been set
	 */
	Class<?> getWrappedClass();

	/**
	 * Obtain the PropertyDescriptors for the wrapped object	//获取包裹对象的PropertyDescriptors
	 * (as determined by standard JavaBeans introspection).		//由标准java bean内省机制决定
	 * @return the PropertyDescriptors for the wrapped object
	 */
	PropertyDescriptor[] getPropertyDescriptors();

	/**
	 * Obtain the property descriptor for a specific property
	 * of the wrapped object.
	 * @param propertyName the property to obtain the descriptor for
	 * (may be a nested path, but no indexed/mapped property)
	 * @return the property descriptor for the specified property
	 * @throws InvalidPropertyException if there is no such property
	 */
	PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException;

	/**
	 * Set whether this BeanWrapper should attempt to "auto-grow" a
	 * nested path that contains a {@code null} value.
	 * <p>If {@code true}, a {@code null} path location will be populated
	 * with a default object value and traversed instead of resulting in a
	 * {@link NullValueInNestedPathException}. Turning this flag on also enables
	 * auto-growth of collection elements when accessing an out-of-bounds index.
	 * <p>Default is {@code false} on a plain BeanWrapper.
	 */
	void setAutoGrowNestedPaths(boolean autoGrowNestedPaths);

	/**
	 * Return whether "auto-growing" of nested paths has been activated.
	 */
	boolean isAutoGrowNestedPaths();

	/**
	 * Specify a limit for array and collection auto-growing.
	 * <p>Default is unlimited on a plain BeanWrapper.
	 */
	void setAutoGrowCollectionLimit(int autoGrowCollectionLimit);

	/**
	 * Return the limit for array and collection auto-growing.
	 */
	int getAutoGrowCollectionLimit();

}
