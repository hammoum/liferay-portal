/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.language;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Carlos Sierra Andrés
 */
public class AggregateResourceBundle extends ResourceBundle {

	public AggregateResourceBundle(ResourceBundle... resourceBundles) {
		_resourceBundles = resourceBundles;
	}

	@Override
	public boolean containsKey(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		for (ResourceBundle resourceBundle : _resourceBundles) {
			if (resourceBundle.containsKey(key)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(handleKeySet());
	}

	@Override
	protected Object handleGetObject(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		for (ResourceBundle resourceBundle : _resourceBundles) {
			if (!resourceBundle.containsKey(key)) {
				continue;
			}

			try {
				return resourceBundle.getObject(key);
			}
			catch (MissingResourceException mre) {
			}
		}

		return null;
	}

	@Override
	protected Set<String> handleKeySet() {
		if (_keys == null) {
			_keys = new HashSet<String>();

			for (ResourceBundle resourceBundle : _resourceBundles) {
				_keys.addAll(resourceBundle.keySet());
			}
		}

		return _keys;
	}

	private Set<String> _keys;
	private final ResourceBundle[] _resourceBundles;

}