package com.iagl.opl.medt;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Launcher {
	
	public static void main(String[] args) throws ClassNotFoundException, MalformedURLException {
		
		File f = new File(args[0]);
		URL url = f.toURI().toURL();
		URL[] urls = {url};
		ClassLoader loader = new URLClassLoader(urls);
		
		Class<?> clazz = loader.loadClass(args[1]);
		
		MagicalExperimentalDebuggingTool medt = new MagicalExperimentalDebuggingTool(clazz);
		
		medt.debugClass();
				
	}

}
