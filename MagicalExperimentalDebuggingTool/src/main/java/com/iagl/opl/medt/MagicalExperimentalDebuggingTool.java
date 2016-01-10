package com.iagl.opl.medt;

import java.awt.Point;
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
import spoon.processing.Processor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;

public class MagicalExperimentalDebuggingTool {
	
	// the class of test
	private static Class<?> TEST_CLASS;
	
	// the tested class (class tested by the class of test)
	private static Class<?> TESTED_CLASS;
	
	// the current method to spoon
	private static String CURRENT_METHOD;
		
	// list of failures
	private List<Failure> current_failures;
	
	// list of failure's line number
	private List<Integer> failures_lines;
	
	// list of aborts methods
	private Set<String> aborts;
	
	private String mpath = ""; // Only use for intern tests
	
	// list of processors applied by MEDT
	private Processor[] procs = {new ReallocationOverSightProcessor()};
	
	public MagicalExperimentalDebuggingTool(Class<?> clazz) {
		TEST_CLASS = clazz;
		aborts = new HashSet<String>();
	}
	
	// Constructor only use for intern tests
	public MagicalExperimentalDebuggingTool(Class<?> clazz, String path) {
		TEST_CLASS = clazz;
		mpath = path;
		aborts = new HashSet<String>();
	}
	
	/**
	 * Try to debug the class
	 */
	public void debugClass() {
		// we run tests
		runTestClass();
		// we try to retrieve the tested class
		calculateTestedClass();
		
		// Path of the class to spoon
		String input = String.format("%s/%s%s.java", System.getProperty("user.dir")
				, mpath, getTestedClass().getName().replace(".", "/"));
		
		
		while (!getTestedProblematicMethods().isEmpty()) {
			CURRENT_METHOD = String.valueOf(getTestedProblematicMethods().toArray()[0]);
		
			for (int i=0; i < procs.length; i++) {
		
				boolean first_start = true;
				
				while (first_start || !getActualPermutation().equals(new Point(0,0))) {
				
					if (first_start)
						first_start = false;
					
					Launcher l = new Launcher();		
			        l.addInputResource(input);
			        l.addProcessor(procs[i]);
			        l.run();
			         
			        CtClass c = (CtClass) l.getFactory().Package().getRootPackage().getElements(new NameFilter(getTestedClass().getSimpleName())).get(0);
			        System.out.println(c);
			        
			        // COMMENT RUNNER LES TESTS SUR LA CLASSE SPOONEE ???
					
					if (regressions() == 1) {
						//	RIEN
					}
			         else {
			        	//REMETTRE L'ANCIENNE CLASSE
			         }
			     
				}
			}

			// TOUTES LES POSSIBILITES DE MUTATIONS ONT ETE EXPLOITES : ABANDON POUR LA METHODE
			aborts.add(CURRENT_METHOD);
			
		}
			
		
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
	private int regressions() {
		
		List<Integer> last_result = getFailuresLines();
				
		MagicalExperimentalDebuggingTool medt = new MagicalExperimentalDebuggingTool(TEST_CLASS,mpath);
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
		
		failures_lines = new_result;
		current_failures = medt.current_failures;
		
		return 1;
	}
	
	public static Class<?> getTestedClass() {
		return TESTED_CLASS;
	}
	
	private static Class<?> calculateTestedClass() {
		
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
		
		if (clazz.size() == 1) {
			TESTED_CLASS = clazz.get(0);
			return clazz.get(0);
		}
		
		for (Class<?> c : clazz) {
			if (TEST_CLASS.getName().toLowerCase().contains(c.getName().toLowerCase())) {
				TESTED_CLASS = c;
				return c;
			}
		}
		
		System.out.println("Cannot found tested class. Your program is so bad !");
		return null;
	}
	
	/**
	 * Get all problematic methods in the class of test
	 * @return a set of all method name
	 */
	private Set<String> getProblematicMethods() {
		// we initialize the return set
		Set<String> set = new HashSet<String>();
		
		// for each failure
		for (Failure f : this.current_failures) {
			// and for each element in the stacktrace
			StackTraceElement[] tab = f.getException().getStackTrace();
			for (int i=0; i < tab.length; i++) {
				// if we find the same class name in the stacktrace, add the method name in the return set
				if (tab[i].getClassName().equals(TEST_CLASS.getName())) {
					set.add(tab[i].getMethodName().toLowerCase());
				}
			}
		}
		// finally we return the return set
		return set;
	}
	
	/**
	 * Get all problematic methods tested in the class of test
	 * @return a set of method name
	 */
	private  Set<String> getTestedProblematicMethods() {
		// we retrieve all problematic method in the class of test
		Set<String> set = getProblematicMethods();
		// we initialize the return set
		Set<String> testedset = new HashSet<String>();
		
		// we retrieve the tested class
		Class<?> clazz = getTestedClass();
		
		// if the tested class is not found
		if (clazz == null) {
			System.out.println("Cannot found tested problematic methods. Your program is so bad !");
		}
		else {
			// if the tested class is found, for each method m of this class
			for (Method m : clazz.getMethods()) {
				// for each problematic method s of the class of test
				for (String s : set) {
					// we compare s and m (if m contains s)
					if (s.contains(m.getName().toLowerCase()))
						// moreover, the method mustn't be abordted
						if (!aborts.contains(m.getName()))
							// if all conditions if ok, add in the return set
							testedset.add(m.getName());
				}
			}
		}
		// finally we return the return set
		return testedset;
	}
	
	/**
	 * Get all failure's number line
	 * @return a list with all number line of failures
	 */
	private List<Integer> getFailuresLines() {
		return failures_lines;
	}
	
	/**
	 * Get the current method to spoon
	 * @return the name of the current method to spoon
	 */
	public static String getCurrentMethod() {
		return CURRENT_METHOD;
	}
	
	// ============================= Permutations Manager ================================
	
	// number of candidates for the actual Spoon processor
	public static int permutations = 0;
	// actual permutation for the actual Spoon processor 
	// ex :
	// - (1,1) : spooned the first candidate
	// - (1,2) : spooned the first and the second candidate
	// - (2,2) : spooned the second candidate
	private static Point actual_permutation = new Point(0,0);
	
	/**
	 * Add a candidate
	 */
	public static void incrPermutations() {
		permutations++;
		actual_permutation = new Point(1,1);
	}
	
	/**
	 * Pass to the next couple of permutation
	 */
	public static void nextPermutation() {
		int min = actual_permutation.x;
		int max = actual_permutation.y;
		
		// if it's the maximal couple: reset 
		if (min == permutations && max == permutations) {
			actual_permutation = new Point(0,0);
			permutations = 0;
		}

		else if (max == permutations) {
			actual_permutation.setLocation(min+1, min+1);
		}
		else {
			actual_permutation.setLocation(min, max+1);
		}
	}
	
	/**
	 * Get the actual permutation to spoon
	 * @return a point which represent the interval of candidates to spoon
	 */
	public static Point getActualPermutation() {
		return actual_permutation;
	}
	
	
}
