package com.iagl.opl.medt.processors;

import java.util.List;
import java.util.Set;

import com.iagl.opl.medt.MagicalExperimentalDebuggingTool;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

public class ReallocationOverSightProcessor extends AbstractProcessor<CtClass> {

	@Override
	public boolean isToBeProcessed(CtClass element) {

		//We get the name of the tested class
		String name = MagicalExperimentalDebuggingTool.getTestedClass().getSimpleName();

		// we get the package of the tested class
		String packageName = MagicalExperimentalDebuggingTool.getTestedClass().getPackage().getName();

		//if the name and the package of the tested class is same as the class to be processed then we can start the processor, else we pass to the next class
		if (name.equals(element.getSimpleName()) && packageName.equals(element.getPackage().getQualifiedName()))
			return true;
		else
			return false;

	}

	/**
	 * First we get all the method
	 * for each class, we look for the method to spoon
	 * 
	 * 
	 */
	public void process(CtClass element) {

		// gettig all the method in this class 'element'
		Set<CtMethod> methods = element.getAllMethods();

		//we get all problematic method tested previously
		Set<String> toSpoon = MagicalExperimentalDebuggingTool.getTestedProblematicMethods();

		for (CtMethod m : methods) {
			//is the method contain in the list of method to spoon
			if (toSpoon.contains(m.getSimpleName())) {
				Filter<CtInvocation> filter = new TypeFilter(CtInvocation.class);
				List<CtInvocation> invocations = m.getElements(filter);

				// getting all invocation in this method
				for (CtInvocation invocation : invocations) {

					if (invocation.getParent() instanceof CtBlock) {

						//if the format of invocation is ok, we apply the solution.
						if (correctFormat(invocation)) {
							//we try to change the operation by stringObject = Operation 
							String new_code = String.format("%s = %s", invocation.getTarget().toString(),
									invocation.toString());

							CtCodeSnippetStatement newStatement = getFactory().Core().createCodeSnippetStatement();
							newStatement.setValue(new_code);

							invocation.replace(newStatement);
						}
					}

				}

			}			

		}

	}

	/**
	 * Verifying the invocation.
	 * if the type valid for our processor (in this case 'java.lang.String').
	 * and if the operation is supported by our processor
	 * "substring"
	 * "replace"
	 * "replaceAll"
	 * "replaceFirst"
	 * "subSequence"
	 * "toLowerCase"
	 * "toUpperCase"
	 * @param invocation
	 * @return true if valid false esle.
	 */
	private boolean correctFormat(CtInvocation invocation) {

		String name = invocation.getExecutable().getSimpleName();
		String type = invocation.getType().getQualifiedName();

		if (!type.equals("java.lang.String"))
			return false;

		if (name.equals("concat"))
			return true;

		if (name.equals("substring"))
			return true;

		if (name.equals("replace"))
			return true;

		if (name.equals("replaceAll"))
			return true;

		if (name.equals("replaceFirst"))
			return true;

		if (name.equals("subSequence"))
			return true;

		if (name.equals("toLowerCase"))
			return true;

		if (name.equals("toUpperCase"))
			return true;

		return false;
	}

}
