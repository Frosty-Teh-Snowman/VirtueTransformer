package org.virtue.utility.refactor;

public class InterfaceMappingData {
	
	protected InterfaceMappingData[] superInterfaces;
	protected String interfaceName;
	
	public InterfaceMappingData(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	public InterfaceMappingData(String interfaceName, InterfaceMappingData... superInterfaces) {
		this.interfaceName = interfaceName;
		this.superInterfaces = superInterfaces;
	}
	
	public String getInterfaceName() {
		return interfaceName;
	}
	
	public InterfaceMappingData setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
		return this;
	}
	
	public InterfaceMappingData[] getSuperInterfaces() {
		return superInterfaces;
	}
	
	public InterfaceMappingData setSuperInterfaces(InterfaceMappingData... superInterfaces) {
		this.superInterfaces = superInterfaces;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((interfaceName == null) ? 0 : interfaceName.hashCode());
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
		InterfaceMappingData other = (InterfaceMappingData) obj;
		if (interfaceName == null) {
			if (other.interfaceName != null)
				return false;
		} else if (!interfaceName.equals(other.interfaceName))
			return false;
		return true;
	}
}