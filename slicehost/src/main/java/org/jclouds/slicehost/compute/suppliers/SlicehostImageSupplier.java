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

package org.jclouds.slicehost.compute.suppliers;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.slicehost.SlicehostClient;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SlicehostImageSupplier implements Supplier<Set<? extends Image>> {
   public static final Pattern SLICEHOST_PATTERN = Pattern.compile("(([^ ]*) .*)");

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final SlicehostClient sync;
   private final Supplier<Location> location;

   @Inject
   SlicehostImageSupplier(SlicehostClient sync, Supplier<Location> location) {
      this.sync = sync;
      this.location = location;
   }

   @Override
   public Set<? extends Image> get() {
      final Set<Image> images = Sets.newHashSet();
      logger.debug(">> providing images");
      for (final org.jclouds.slicehost.domain.Image from : sync.listImages()) {
         String version = null;
         Matcher matcher = SLICEHOST_PATTERN.matcher(from.getName());

         OsFamily osFamily = null;
         String osName = null;
         String osArch = null;
         String osVersion = null;
         String osDescription = from.getName();
         boolean is64Bit = true;

         if (from.getName().indexOf("Red Hat EL") != -1) {
            osFamily = OsFamily.RHEL;
         } else if (from.getName().indexOf("Oracle EL") != -1) {
            osFamily = OsFamily.OEL;
         } else if (matcher.find()) {
            try {
               osFamily = OsFamily.fromValue(matcher.group(2).toLowerCase());
            } catch (IllegalArgumentException e) {
               logger.debug("<< didn't match os(%s)", matcher.group(2));
            }
         }
         OperatingSystem os = new OperatingSystem(osFamily, osName, osVersion, osArch, osDescription, is64Bit);

         images.add(new ImageImpl(from.getId() + "", from.getName(), from.getId() + "", location.get(), null,
                  ImmutableMap.<String, String> of(), os, from.getName(), version, new Credentials("root", null)));
      }
      logger.debug("<< images(%d)", images.size());
      return images;
   }
}