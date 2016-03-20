package com.sample.cdi;

import javax.ejb.Local;

@Local
public interface IAnotherService {
	
	public String callNestedEJB();
}
