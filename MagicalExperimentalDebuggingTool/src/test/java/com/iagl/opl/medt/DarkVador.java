package com.iagl.opl.medt;

public class DarkVador {
	
	
	public DarkVador() {
		
	}
	
	public String sayImYourFather(String name) {
		String end = ", I'm your father !";
		
		name.concat(end);
				
		return name;
	}
	
	public String getSide() {
		String side = "dark side";
		
		side.substring(0, 3);
		
		side.toUpperCase();
		
		return side;
	}

}
