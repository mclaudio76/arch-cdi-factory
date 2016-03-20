package com.sample.cdi;
import javax.ejb.Local;

@Local
public interface ICommon {
    public String getGreetings();
}
