
package com.skrrrrr.harudam.common.enums;

public enum UserType {
	PARENT("ROLE_PARENT"),
	CHILD("ROLE_CHILD");
	
	private final String roleName;
	UserType(String roleName) { this.roleName = roleName; }
	public String getRoleName() { return roleName; }
}
