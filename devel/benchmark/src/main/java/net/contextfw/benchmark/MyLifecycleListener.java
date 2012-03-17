package net.contextfw.benchmark;

import java.lang.reflect.Method;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.LifecycleListener;

import com.google.inject.Singleton;


@Singleton
public class MyLifecycleListener implements LifecycleListener {

	private void log(String phase) {
//		System.out.println(Thread.currentThread().getName()
//				+ "\t"
//				+ phase
//				+ "\t"
//				+ System.currentTimeMillis());
	}
	
	@Override
	public void beforeInitialize() {
		log("Initialize");
	}

	@Override
	public void afterInitialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean beforeUpdate() {
		return true;
	}

	@Override
	public void afterUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onException(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeRender() {
		log("Before render");
	}

	@Override
	public void afterRender() {
		log("After render");
	}

    @Override
    public boolean beforeRemotedMethod(Component component, Method method, Object[] args) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void afterRemoteMethod(Component component, Method method, RuntimeException thrown) {
        // TODO Auto-generated method stub
        
    }
}