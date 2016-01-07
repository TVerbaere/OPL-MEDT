package com.iagl.opl.medt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class MagicalExperimentalDebugTool {
	
	private final Class<?> TEST_CLASS;
	
	private List<Failure> current_failures;
	
	public MagicalExperimentalDebugTool(Class<?> clazz) {
		TEST_CLASS = clazz;
		current_failures = new ArrayList<Failure>();
	}
	
	
	public void debugClass() {
		//DO JOB
			
		System.out.println("NY");
	}
	
	public List<Integer> runTestClass() {
		for (Method m : TEST_CLASS.getDeclaredMethods()) {
			if (m.getAnnotations()[0].annotationType().getName().equals("org.junit.Test")) {
				this.runTest(m.getName());
			}
		}
		
		List<Integer> problems = this.problematicAsserts();
		current_failures = new ArrayList<Failure>();
		
		return problems;
		
	}
	
	private void runTest(String testname) {
		
		Request request = Request.method(TEST_CLASS,testname);
		Result result = new JUnitCore().run(request);
		
		current_failures.addAll(result.getFailures());
		
	}
	
	private int countCurrentFailures() {
		return current_failures.size();
	}
	
	private List<Integer> problematicAsserts() {
		List<Integer> lines = new ArrayList<Integer>();
		
		for (int i=0; i < this.countCurrentFailures(); i++) {
			StackTraceElement[] stacktrace = current_failures.get(i).getException().getStackTrace();
			int j = 0;
			boolean stop = false;
	        while (j <stacktrace.length && !stop) {
	        	if(stacktrace[j].getClassName().equals(TEST_CLASS.getName())) {
	        		lines.add(stacktrace[j].getLineNumber());
	        		stop = true;
	        	}
	        	j++;
	        }
			
		}
		
		return lines;
			
	}
	
	/**
	 * 0 : no change
	 * -1 : regression
	 * 1 : improvement
	 * 
	 * @param last_result
	 * @return
	 */
	public int regressions(List<Integer> last_result) {
		List<Integer> new_result = this.runTestClass();
		
		if (last_result.equals(new_result))
			return 0;
		
		if (new_result.size() >= last_result.size())
			return -1;
		
		
		return 1;
	}
	
	
}
