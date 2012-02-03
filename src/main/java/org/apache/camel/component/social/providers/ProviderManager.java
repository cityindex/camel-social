/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.social.providers;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class ProviderManager {

	private static boolean initialized = false;
	private static Map<String, SocialProvider> providersMap;

	static {
		initialize();
	}

	private ProviderManager() {
	}

	private static synchronized void initialize() {
		if (!initialized) {
			initialized = true;
			providersMap = new Hashtable<String, SocialProvider>();
			loadProviders();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static synchronized void loadProviders() {
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				ProviderLoader loadedProviders = ProviderLoader.load();
				Iterator<SocialProvider> providersIterator = loadedProviders.iterator();

				try {
					while (providersIterator.hasNext()) {
						SocialProvider provider = providersIterator.next();
						providersMap.put(provider.getProviderName(), provider);
					}
				} catch (Throwable t) {
				}
				return null;
			}
		});
	}

	public static SocialProvider locate(String provider) {
		return providersMap.get(provider);
	}

	public static void registerProvider(SocialProvider provider) {
		providersMap.put(provider.getProviderName(), provider);
	}

}
