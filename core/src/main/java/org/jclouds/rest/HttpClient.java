/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.rest;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.io.Payload;

/**
 * Simple client
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface HttpClient {

   /**
    * @return eTag
    */
   String put(URI location, Payload payload);

   /**
    * @return eTag
    */
   String put(URI location, Payload payload, HttpRequestOptions options);

   /**
    * @return eTag
    */
   String post(URI location, Payload payload);

   /**
    * @return eTag
    */
   String post(URI location, Payload payload, HttpRequestOptions options);

   boolean exists(URI location);

   /**
    * @return null if the resource didn't exist.
    */
   InputStream get(URI location);
   
   InputStream invoke(HttpRequest location);
   
   InputStream get(URI location, HttpRequestOptions options);

   /**
    * @return false if the resource didn't exist.
    */
   boolean delete(URI location);
}
