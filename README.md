# arch-cdi-factory
CDI Factory for EJB 3.x based application services.
Release notes
=============

This project implements a proof-of-concept CDI Factory for EJB based services, where:

- A given service interface is implemented by multiple EJBs and service interface is used as local interface;

- For each service interface, any EJB is either the "standard" implementation  or  a "custom implementation" valid in a given "enviroment" 
  (production, test,  specific customization and so on);

- EJBs are "versioned" by applying a custom annotation (@ApplicationService) on the bean class; 

- For each service interface, there must be at least one EJB tagged with @ApplicationService(Version.BASE) to provide a default implementation;
  
- We want loosely coupled dependencies among services: we want to use interfaces and let the container initialize the right implementation with respect to 
  an enviroment variable we can set (for example) as  Application Server's custom property. In other words, whenever we write:
  	
  	@Inject
  	@ApplicationService
  	MyServiceInterface actualImpl;
  	
  we are actually injecting a specific implementation of MyServiceInterface, which one is choosen depends only upon an AppServer parameters.
  
- We want injection mechanism to be "smart" : if a customized version for a service exists, then the corrisponding implementation is injected; otherwise, the standard implementation 
  will be injected.

-------
Requirements: JDK 1.8+

Tested on: JBoss WildFly 10.0 final (http://wildfly.org)


