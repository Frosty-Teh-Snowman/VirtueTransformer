package org.virtue.utility.refactor;

import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodMappingData extends Identifiable {
	
	protected String methodOwner;
	protected MappingData methodName;
	protected String methodDesc;
	protected boolean isStatic;
	
	public MethodMappingData(MappingData methodName, String methodDesc, boolean isStatic) {
		this("", methodName, methodDesc, isStatic);
	}
	
	public MethodMappingData(String methodOwner, MappingData methodName, String methodDesc, boolean isStatic) {
		this.methodOwner = methodOwner;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
		this.isStatic = isStatic;
	}
	
	public String getMethodOwner() {
		return methodOwner;
	}
	
	public MethodMappingData setMethodOwner(String methodOwner) {
		this.methodOwner = methodOwner;
		return this;
	}
	
	public MappingData getMethodName() {
		return methodName;
	}
	
	public MethodMappingData setMethodName(MappingData methodName) {
		this.methodName = methodName;
		return this;
	}
	
	public String getMethodDesc() {
		return methodDesc;
	}
	
	public MethodMappingData setMethodDesc(String methodDesc) {
		this.methodDesc = methodDesc;
		return this;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public MethodMappingData setStatic(boolean isStatic) {
		this.isStatic = isStatic;
		return this;
	}
	
	@Override
	public MethodMappingData identify() {
		super.identify();
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (isStatic ? 1231 : 1237);
		result = (prime * result) + ((methodDesc == null) ? 0 : methodDesc.hashCode());
		result = (prime * result) + ((methodName == null) ? 0 : methodName.hashCode());
		result = (prime * result) + ((methodOwner == null) ? 0 : methodOwner.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodMappingData other = (MethodMappingData) obj;
		if (isStatic != other.isStatic)
			return false;
		if (methodDesc == null) {
			if (other.methodDesc != null)
				return false;
		} else if (!methodDesc.equals(other.methodDesc))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (methodOwner == null) {
			if (other.methodOwner != null)
				return false;
		} else if (!methodOwner.equals(other.methodOwner))
			return false;
		return true;
	}
}