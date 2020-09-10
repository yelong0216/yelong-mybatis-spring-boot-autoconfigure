/**
 * 
 */
package org.yelong.spring.boot;

import java.io.PrintStream;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

/**
 * @since 1.0.1
 */
public class YelongBanner implements Banner {

	public static final Banner INSTANCE = new YelongBanner();

	protected YelongBanner() {
	}

	public static final String PRINT = "_____.___.      .____                          \r\n"
			+ "\\__  |   | ____ |    |    ____   ____    ____  \r\n"
			+ " /   |   |/ __ \\|    |   /  _ \\ /    \\  / ___\\ \r\n"
			+ " \\____   \\  ___/|    |__(  <_> )   |  \\/ /_/  >\r\n"
			+ " / ______|\\___  >_______ \\____/|___|  /\\___  / \r\n"
			+ " \\/           \\/        \\/          \\//_____/  \r\n";

	@Override
	public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
		out.print(PRINT);
	}

}
