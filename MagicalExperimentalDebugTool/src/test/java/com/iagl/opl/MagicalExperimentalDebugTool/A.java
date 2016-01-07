package com.iagl.opl.MagicalExperimentalDebugTool;

public class A {
	
	private String toto;
	private int john;
	
	public A(){
		
		toto = "toto";
		john =1; 
		
	}
	
	/**
	 * This method is suposed to add 1 to john
	 */
	public void upJohn(){
		this.john--;
	}
	
	public String getToto() {
		return toto;
	}

	public int getJohn() {
		return john;
	}

	/**
	 * this methode set null to toto
	 */
	public void eraseToto(){
		toto = null;
	}
	
	/**
	 * get the size of toto
	 * @return toto.size
	 */
	public int totoSize(){
		return toto.length();
	}
}
