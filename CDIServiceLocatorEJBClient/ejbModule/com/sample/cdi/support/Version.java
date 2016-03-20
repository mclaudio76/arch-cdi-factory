package com.sample.cdi.support;

public enum Version {
	// Base (standard) implementation of any ApplicationService interface.
	// For any interface, there must be one and only one EJB annotated @ApplicationService(Version.BASE) to avoid ambigous references. 
	BASE,
	
	// Default value for @ApplicationService annotation.
	CURRENT_CONFIG,

	// Place here a value for any custom scenario
	CUSTOMIZED,
	TEST,
	PRODUCTION;
	
	
	
}
