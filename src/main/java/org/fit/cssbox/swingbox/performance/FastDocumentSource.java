/*
 * Copyright 2020 White Magic Software, Ltd.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.fit.cssbox.swingbox.performance;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.fit.cssbox.io.DocumentSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class FastDocumentSource extends DocumentSource {
  private final CloseableHttpClient mClient;

  private URL mUrl;
  private InputStream mInputStream;
  private String mContentType = "";

  public FastDocumentSource( final URL url ) throws IOException {
    super( url );
    assert url != null;

    mClient = createHttpClient();
    mUrl = url;
  }

  public FastDocumentSource setURL(final URL url) {
    mUrl = url;

    return this;
  }

  private CloseableHttpClient createHttpClient() {
    final var cacheLifetime = TimeUnit.HOURS.toSeconds( 1 );

    final var cacheConfig =
        CacheConfig.custom()
                   .setMaxCacheEntries( 1000 )
                   .setMaxObjectSize( 120 * 1024 )
                   .setHeuristicCachingEnabled( true )
                   .setHeuristicDefaultLifetime( cacheLifetime )
                   .build();

    final var cacheStore = new BasicHttpCacheStorage( cacheConfig );

    final var builder =
        CachingHttpClients.custom()
                          .setCacheConfig( cacheConfig )
                          .setHttpCacheStorage( cacheStore );

    return builder.build();
  }

  @Override
  public URL getURL() {
    return mUrl;
  }

  @Override
  public String getContentType() {
    return mContentType;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    final URI uri;

    try {
      uri = getURL().toURI();
    } catch( final Exception e ) {
      throw new IOException( e );
    }

    final var httpGet = new HttpGet( uri );
    final var response = mClient.execute( httpGet );

    final var entity = response.getEntity();
    entity.getContentType();

    final var contentType = ContentType.getOrDefault( entity );
    mContentType = contentType.getMimeType();

    return mInputStream = entity.getContent();
  }

  @Override
  public void close() throws IOException {
    if( mInputStream != null ) {
      mInputStream.close();
    }
  }
}
