package com.sample.cdi;

import javax.ejb.Stateless;

import com.sample.cdi.support.ApplicationService;
import com.sample.cdi.support.Version;

/**
 * Session Bean implementation class FirstBean
 */
@Stateless
@ApplicationService(Version.BASE)
public class FirstBean implements ICommon  {

    /**
     * Default constructor. 
     */
    public FirstBean() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public String getGreetings() {
		 return " This is a greeting from BASE implementation.";
	}

}
