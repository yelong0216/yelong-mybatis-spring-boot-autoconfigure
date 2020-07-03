/**
 * 
 */
package org.yelong.mybatis.spring.boot.autoconfigure;

import java.io.PrintStream;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

/**
 * @author PengFei
 * @since 1.0.1
 */
public final class YelongBanner implements Banner {

	public static final Banner YELONG_BANNER = new YelongBanner();

	private YelongBanner() {
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
