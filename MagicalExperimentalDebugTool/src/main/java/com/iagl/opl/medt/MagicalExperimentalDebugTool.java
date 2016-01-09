package com.iagl.opl.medt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.iagl.opl.medt.processors.ReallocationOverSightProcessor;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;

public class MagicalExperimentalDebugTool {
	
	private static Class<?> TEST_CLASS;
	
	private static List<Failure> current_failures;
	
	private List<Integer> failures_lines;
	
	private String mpath = "";
	
	public MagicalExperimentalDebugTool(Class<?> clazz) {
		TEST_CLASS = clazz;
	}
	
	public MagicalExperimentalDebugTool(Class<?> clazz, String path) {
		TEST_CLASS = clazz;
		mpath = path;
		
	}
	
	public void debugClass() {
		
		runTestClass();
			
		String input = String.format("%s/%s%s.java", System.getProperty("user.dir")
				, mpath, getTestedClass().getName().replace(".", "/"));
						
		Launcher l = new Launcher();
					
        l.addInputResource(input);
        l.addProcessor(new ReallocationOverSightProcessor());
        l.run();
         
        CtClass c = (CtClass) l.getFactory().Package().getRootPackage().getElements(new NameFilter(getTestedClass().getSimpleName())).get(0);
		
        System.out.println(c);
        
		//Class<?> spoonClazz = c.getClass(); // A CHANGER !
		
		//if (regressions(spoonClazz) == 1)
		//	System.out.println(spoonClazz);// Sauvegarder les modification
		
	}
	
	private void runTestClass() {
				
		current_failures = new ArrayList<Failure>();
		failures_lines = new ArrayList<Integer>();
		
		for (Method m : TEST_CLASS.getDeclaredMethods()) {
			if (m.getAnnotations()[0].annotationType().getName().equals("org.junit.Test")) {
				runTest(m.getName());
			}
		}
		
		failures_lines = problematicAsserts();
			
		
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
		
		for (int i=0; i < countCurrentFailures(); i++) {
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
	private int regressions(Class<?> newClazz) {
		
		List<Integer> last_result = getFailuresLines();
		
		MagicalExperimentalDebugTool medt = new MagicalExperimentalDebugTool(newClazz);
		medt.runTestClass();
		List<Integer> new_result = medt.getFailuresLines();
		
		if (last_result.equals(new_result))
			return 0;
		
		if (new_result.size() >= last_result.size())
			return -1;
		
		for (Integer i : new_result) {
			if (!last_result.contains(i))
				return -1;
		}
		
		return 1;
	}
	
	private static Class<?> getTestedClass() {
		
		List<Class<?>> clazz = new ArrayList<Class<?>>();
		
		// Hypothèse 1 : La classe testée est déclarée en attribut dans la classe de test
		// (= initialisation dans un setUp, ignore le cas où la classe testée est redéfinie à chaque test localement)
		
		// Hypothèse 2 : Le package de la classe de test est identique au package de la classe testée
		// (= bonne pratique des tests unitaires)
		
		// Hypothèse 3 : Le nom de la méthode testée se trouve dans le nom de la méthode de test
		// (= bonne pratique des tests utnitaires)
				
		for (Field f :TEST_CLASS.getDeclaredFields()) {
			if (f.getType().getPackage().equals(TEST_CLASS.getPackage())) {
				clazz.add(f.getType());
			}
		}
		
		if (clazz.isEmpty())
			System.out.println("Cannot found tested class. Your program is so bad !");
		
		if (clazz.size() == 1)
			return clazz.get(0);
		
		for (Class<?> c : clazz) {
			if (TEST_CLASS.getName().toLowerCase().contains(c.getName().toLowerCase()))
				return c;
		}
		
		System.out.println("Cannot found tested class. Your program is so bad !");
		return null;
	}
	
	private static Set<String> getProblematicMethods() {
		
		Set<String> set = new HashSet<String>();
		
		for (Failure f : current_failures) {
			StackTraceElement[] tab = f.getException().getStackTrace();
			for (int i=0; i < tab.length; i++) {
				if (tab[i].getClassName().equals(TEST_CLASS.getName())) {
					set.add(tab[i].getMethodName().toLowerCase());
				}
			}
		}
		
		return set;
	}
	
	public static Set<String> getTestedProblematicMethods() {
		Set<String> set = getProblematicMethods();
		Set<String> testedset = new HashSet<String>();
		
		Class<?> clazz = getTestedClass();
		
		if (clazz == null) {
			System.out.println("Cannot found tested problematic methods. Your program is so bad !");
		}
		else {
			for (Method m : clazz.getMethods()) {
				for (String s : set) {
					if (s.contains(m.getName().toLowerCase()))
						testedset.add(m.getName());
				}
			}
		}
		
		return testedset;
	}
	
	private List<Integer> getFailuresLines() {
		return failures_lines;
	}
	
}
