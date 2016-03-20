package com.sample.cdi;

import javax.ejb.Stateless;

import com.sample.cdi.support.ApplicationService;
import com.sample.cdi.support.Version;

/**
 * Session Bean implementation class SecondBean
 */
@Stateless
@ApplicationService(Version.PRODUCTION)
public class SecondBean implements ICommon {

    /**
     * Default constructor. 
     */
    public SecondBean() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public String getGreetings() {
		return " This is a greeting from PRODUCTION (Second Bean) impl.";
	}

}
