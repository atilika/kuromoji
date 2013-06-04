package com.atilika.kuromoji;

import java.io.IOException;
import java.io.InputStream;

/**
 * Applies a given prefix to the resources passed to a given resolver.
 */
final class PrefixDecoratorResolver implements ResourceResolver {
	private final ResourceResolver delegate;
	private final String prefix;

	PrefixDecoratorResolver(String prefix, ResourceResolver resolver) {
		assert prefix != null;
		assert resolver != null;

		this.delegate = resolver;
		this.prefix = prefix;
	}

	@Override
	public InputStream resolve(String resourceName) throws IOException {
		return delegate.resolve(prefix + resourceName);
	}
}
