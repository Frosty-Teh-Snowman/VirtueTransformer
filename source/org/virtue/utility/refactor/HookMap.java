package org.virtue.utility.refactor;

import java.util.ArrayList;
import java.util.List;

public class HookMap {
	
	protected List<ClassMappingData> classes;
	protected List<FieldMappingData> fields;
	protected List<MethodMappingData> methods;
	
	public HookMap() {
		classes = new ArrayList<ClassMappingData>();
		fields = new ArrayList<FieldMappingData>();
		methods = new ArrayList<MethodMappingData>();
	}
	
	public void addClass(ClassMappingData MappingData) {
		classes.add(MappingData);
	}
	
	public void addField(FieldMappingData field) {
		fields.add(field);
	}
	
	public void addMethod(MethodMappingData method) {
		methods.add(method);
	}
	
	public List<ClassMappingData> getClasses() {
		return classes;
	}
	
	/*public MappingData getClassByObfuscatedName(String obfName) {
		for (MappingData mappingData : classes) {
			if (mappingData.obfuscatedName.equals(obfName))
				return mappingData;
		}
		return null;
	}
	
	public MappingData getClassByRefactoredName(String refacName) {
		for (MappingData mappedData : classes) {
			if (mappedData.refactoredName.equals(refacName))
				return mappedData;
		}
		return null;
	}*/
	
	public List<FieldMappingData> getFields() {
		return fields;
	}
	
	/*public List<FieldMappingData> getFieldsByObfuscatedFieldOwner(String obfName) {
		List<FieldMappingData> fields = new ArrayList<FieldMappingData>();
		for (FieldMappingData field : this.fields) {
			if (field.fieldOwner.equals(obfName))
				fields.add(field);
		}
		return fields;
	}
	
	public List<FieldMappingData> getFieldsByRefactoredFieldOwner(String refacName) {
		List<FieldMappingData> fields = new ArrayList<FieldMappingData>();
		for (FieldMappingData field : this.fields) {
			if (field.fieldOwner.equals(refacName))
				fields.add(field);
		}
		return fields;
	}
	
	public List<FieldMappingData> getFieldsByObfuscatedMethodOwner(String obfName) {
		List<FieldMappingData> fields = new ArrayList<FieldMappingData>();
		for (FieldMappingData field : this.fields) {
			if (field.methodOwner.obfuscatedName.equals(obfName))
				fields.add(field);
		}
		return fields;
	}
	
	public List<FieldMappingData> getFieldsByRefactoredMethodOwner(String refacName) {
		List<FieldMappingData> fields = new ArrayList<FieldMappingData>();
		for (FieldMappingData field : this.fields) {
			if (field.methodOwner.refactoredName.equals(refacName))
				fields.add(field);
		}
		return fields;
	}*/
	
	public List<MethodMappingData> getMethods() {
		return methods;
	}
	
	/*public List<MethodMappingData> getMethodsByObfuscatedFieldOwner(String obfName) {
		List<MethodMappingData> methods = new ArrayList<MethodMappingData>();
		for (MethodMappingData method : this.methods) {
			if (method.methodOwner.obfuscatedName.equals(obfName))
				methods.add(method);
		}
		return methods;
	}
	
	public List<MethodMappingData> getMethodsByRefactoredFieldOwner(String refacName) {
		List<MethodMappingData> methods = new ArrayList<MethodMappingData>();
		for (MethodMappingData method : this.methods) {
			if (method.methodOwner.refactoredName.equals(refacName))
				methods.add(method);
		}
		return methods;
	}
	
	public List<MethodMappingData> getMethodsByObfuscatedMethodOwner(String obfName) {
		List<MethodMappingData> methods = new ArrayList<MethodMappingData>();
		for (MethodMappingData method : this.methods) {
			if (method.callbackOwner.obfuscatedName.equals(obfName))
				methods.add(method);
		}
		return methods;
	}
	
	public List<MethodMappingData> getMethodsByRefactoredMethodOwner(String refacName) {
		List<MethodMappingData> methods = new ArrayList<MethodMappingData>();
		for (MethodMappingData method : this.methods) {
			if (method.callbackOwner.refactoredName.equals(refacName))
				methods.add(method);
		}
		return methods;
	}*/
}