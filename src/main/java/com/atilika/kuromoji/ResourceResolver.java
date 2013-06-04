package com.atilika.kuromoji;

import java.io.IOException;
import java.io.InputStream;

/**
 * An adapter to resolve the required resources into data streams. 
 */
public interface ResourceResolver {
  /**
   * Resolve the resource name and return an open input stream to it.
   */
  InputStream resolve(String resourceName) throws IOException;
}
