package com.sample.cdi;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.sample.cdi.support.ApplicationService;
import com.sample.cdi.support.LookupHelper;

/*****
 * Main CDI Factory.
 *
 * This Singleton EJB exposes a producer method for any @ApplicationService tagged interface
 * we want to inject.
 *
 * Each producer method  calls LookupHelper lookup method passing required interface
 * and an InjectionPoint instance that LookupHelper will process to determinate which Version
 * of the interface is required to be injected.
 *
 * 
 */

@Singleton	
@Startup
public class ServiceLocator {
	
	LookupHelper lookupHelper;
	
    public ServiceLocator() throws Exception  {
      lookupHelper = LookupHelper.getInstance();
    }
    
    @Produces
    @ApplicationService
    public ICommon commonFactory(InjectionPoint injectionPoint) {
    	return (ICommon) lookupHelper.lookup (ICommon.class, injectionPoint);
    }
    
    @Produces
    @ApplicationService
    public IAnotherService anotherServiceFactory(InjectionPoint injectionPoint) {
    	return (IAnotherService) lookupHelper.lookup (IAnotherService.class, injectionPoint);
    }
      
}
    

