package com.atilika.kuromoji;

import java.io.IOException;
import java.io.InputStream;

/**
 * Resolves resources off based on the given class loader.
 */
public final class ClassLoaderResolver implements ResourceResolver {
	private final ClassLoader loader;
	
	public ClassLoaderResolver(ClassLoader loader) {
		this.loader = loader;
	}
	
	public ClassLoaderResolver(Class<?> clazz) {
		this(clazz.getClassLoader());
	}

	@Override
	public InputStream resolve(String resourceName) throws IOException {
		InputStream is = loader.getResourceAsStream(resourceName);
		if (is == null)
			throw new IOException("Classpath resource not found: "
					+ resourceName);
		return is;
	}
}
