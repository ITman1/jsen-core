/**
 * ScriptAnnotationClassMembersResolverFactory.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.jsen.core.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jsen.core.AbstractScriptEngine;
import com.jsen.core.reflect.ClassConstructor;
import com.jsen.core.reflect.ClassField;
import com.jsen.core.reflect.ClassFunction;
import com.jsen.core.reflect.ClassMember;
import com.jsen.core.reflect.ClassMembersResolver;
import com.jsen.core.reflect.ClassMembersResolverFactory;
import com.jsen.core.reflect.DefaultShutter;
import com.jsen.core.reflect.Shutter;

/**
 * Class of the class member resolver factory, which constructs class members resolvers
 * that makes members visible according to annotation notations above them.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ScriptAnnotationClassMembersResolverFactory implements ClassMembersResolverFactory {
	private class ScriptAnnotationClassMembersResolver extends ClassMembersResolver {
		final String pernament_options[] = {ClassMember.PERNAMENT};
		final String pernament_enumerable_options[] = {ClassMember.PERNAMENT, ClassMember.ENUMERABLE};
		
		protected AbstractScriptEngine scriptEngine;
		protected Shutter explicitGrantShutter;
		
		public ScriptAnnotationClassMembersResolver(Class<?> clazz, AbstractScriptEngine browserScriptEngine, Shutter explicitGrantShutter) {
			super(clazz);
			this.scriptEngine = browserScriptEngine;
			this.explicitGrantShutter = explicitGrantShutter;
		}
		
		@Override
		public boolean isObjectGetter(Method method) {
			if (explicitGrantShutter.isMethodVisible(clazz, method) && ClassFunction.isObjectGetterMethod(clazz, method)) {
				return true;
			}
			return ScriptAnnotation.testForObjectGetter(clazz, method, browserScriptEngine);
		}
		
		@Override
		public boolean isGetter(Method method) {
			if (explicitGrantShutter.isMethodVisible(clazz, method) && ClassField.isGetter(method)) {
				return true;
			}
			return ScriptAnnotation.testForScriptGetter(clazz, method, scriptEngine);
		}
		
		@Override
		public boolean isSetter(Method method) {
			if (explicitGrantShutter.isMethodVisible(clazz, method) && ClassField.isSetter(method)) {
				return true;
			}
			return ScriptAnnotation.testForScriptSetter(clazz, method, scriptEngine);
		}
		
		@Override
		public boolean isFunction(Method method) {
			if (explicitGrantShutter.isMethodVisible(clazz, method) && ClassFunction.isFunction(clazz, method)) {
				return true;
			}
			return ScriptAnnotation.isCallable(clazz, method, scriptEngine);
		}	
		
		@Override
		public boolean isField(Field field) {
			if (explicitGrantShutter.isFieldVisible(clazz, field) && ClassField.isField(field)) {
				return true;
			}
			return ScriptAnnotation.testForScriptField(clazz, field, scriptEngine);
		}
		
		@Override
		public boolean isConstructor(Constructor<?> constructor) {
			if (explicitGrantShutter.isConstructorVisible(clazz, constructor) && ClassConstructor.isConstructor(constructor)) {
				return true;
			}
			return ScriptAnnotation.testForScriptConstructor(clazz, constructor, scriptEngine);
		}
		
		@Override
		public String extractFieldNameFromGetter(Method method) {
			return ScriptAnnotation.extractFieldNameFromGetter(method);
		}
		
		@Override
		public String extractFieldNameFromSetter(Method method) {
			return ScriptAnnotation.extractFieldNameFromSetter(method);
		}
		
		@Override
		public String extractFieldName(Field field) {
			return ScriptAnnotation.extractFieldName(field);
		}
	
		@Override
		public String extractFunctionName(Method method) {
			return ScriptAnnotation.extractFunctionName(method);
		}
	
		@Override
		public ClassField constructClassField(Method objectFieldGetter, Method objectFieldSetter, Field field) {
			boolean getOverride = ScriptAnnotation.containsMemberOption(objectFieldGetter, ScriptGetter.FIELD_GET_OVERRIDE);
			boolean setOverride = ScriptAnnotation.containsMemberOption(objectFieldSetter, ScriptSetter.FIELD_SET_OVERRIDE);
			boolean enumerable = ScriptAnnotation.isFieldEnumerable(objectFieldGetter, objectFieldSetter, field);
			String options[] = (enumerable)? pernament_enumerable_options : pernament_options;
			return new ClassField(clazz, objectFieldGetter, objectFieldSetter, field, getOverride, setOverride, options);
		}
	
		@Override
		public ClassFunction constructClassFunction(Method method) {
			boolean enumerable = ScriptAnnotation.isFunctionEnumerable(method);
			String options[] = (enumerable)? pernament_enumerable_options : pernament_options;
			return new ClassFunction(clazz, method, options);
		}

		@Override
		public ClassConstructor constructClassConstructor(Constructor<?> constructor) {
			return new ClassConstructor(clazz, constructor);
		}
	}
	
	protected AbstractScriptEngine browserScriptEngine;
	protected Shutter explicitGrantShutter;
	
	/**
	 * Constructs new class member resolver factory.
	 * 
	 * @param browserScriptEngine Browser script engine that owns this class member resolver factory.
	 */
	public ScriptAnnotationClassMembersResolverFactory(AbstractScriptEngine browserScriptEngine) {
		this(browserScriptEngine, new DefaultShutter());
	}
	
	/**
	 * Constructs new class member resolver factory.
	 * 
	 * @param browserScriptEngine Browser script engine that owns this class member resolver factory.
	 * @param explicitGrantShutter Shutter that makes members explicitly visible instead of script annotation defined above them.
	 */
	public ScriptAnnotationClassMembersResolverFactory(AbstractScriptEngine browserScriptEngine, Shutter explicitGrantShutter) {
		this.browserScriptEngine = browserScriptEngine;
		this.explicitGrantShutter = explicitGrantShutter;
	}
	
	@Override
	public ClassMembersResolver create(Class<?> clazz) {
		return new ScriptAnnotationClassMembersResolver(clazz, browserScriptEngine, explicitGrantShutter);
	}
}
