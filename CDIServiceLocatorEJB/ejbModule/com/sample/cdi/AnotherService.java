package com.sample.cdi;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.sample.cdi.support.ApplicationService;
import com.sample.cdi.support.Version;

@Stateless
@ApplicationService(Version.BASE)
public class AnotherService implements  IAnotherService {

	@Inject	@ApplicationService
	ICommon currentImpl; 
	
	@Override
	public String callNestedEJB() {
		return "From nested EJB: "+currentImpl.getGreetings();//currentImpl.getGreetings();
	}

	
}
