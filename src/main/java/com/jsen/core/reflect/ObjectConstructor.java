/**
 * ObjectConstructor.java
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

package com.jsen.core.reflect;

import java.lang.reflect.Constructor;

/**
 * Represents the constructor of the object.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ObjectConstructor extends ObjectMember<ClassConstructor, Constructor<?>> implements ConstructorMember {
	
	/**
	 * Constructs object constructor for given object using class field.
	 * 
	 * @param object Object containing the object constructor.
	 * @param classFunction Class constructor that is wrapped by this class and associated with the object.
	 */
	public ObjectConstructor(Object object, ClassConstructor classConstructor) {
		super(object, classConstructor);
	}
	
	/**
	 * Constructs object constructor for given object from the passed field.
	 * 
	 * @param object Object containing the object constructor.
	 * @param field Constructor that will be wrapped into class constructor.
	 */
	public ObjectConstructor(Object object, Constructor<?> constructor) {
		this(object, new ClassConstructor(object.getClass(), constructor));
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		return member.getParameterTypes();
	}
}
