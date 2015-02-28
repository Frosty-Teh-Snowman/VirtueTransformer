package org.virtue.utility.refactor;

public class ClassMappingData extends MappingData {
	
	protected InterfaceMappingData[] interfaceDatas;
	
	public ClassMappingData(String refactoredName) {
		super(refactoredName);
	}
	
	public ClassMappingData(String refactoredName, InterfaceMappingData... interfaceDatas) {
		super(refactoredName);
		this.interfaceDatas = interfaceDatas;
	}
	
	public ClassMappingData(String obfuscatedName, String refactoredName, InterfaceMappingData... interfaceDatas) {
		super(obfuscatedName, refactoredName);
		this.interfaceDatas = interfaceDatas;
	}
	
	public InterfaceMappingData[] getInterfaceMappingDatas() {
		return interfaceDatas;
	}
	
	public ClassMappingData setInterfaceData(InterfaceMappingData... interfaceDatas) {
		this.interfaceDatas = interfaceDatas;
		return this;
	}
}