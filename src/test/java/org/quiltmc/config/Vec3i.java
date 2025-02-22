/*
 * Copyright 2022-2024 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.config;

import org.quiltmc.config.api.values.ComplexConfigValue;
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.ValueMap;

import java.util.Objects;

public class Vec3i implements ConfigSerializableObject<ValueMap<Integer>> {
	public final int x, y, z;

	public Vec3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public ComplexConfigValue copy() {
		return this;
	}

	@Override
	public Vec3i convertFrom(ValueMap<Integer> representation) {
		return new Vec3i(representation.get("x"), representation.get("y"), representation.get("z"));
	}

	@Override
	public ValueMap<Integer> getRepresentation() {
		return ValueMap.builder(0)
				.put("x", this.x)
				.put("y", this.y)
				.put("z", this.z)
				.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vec3i vec3i = (Vec3i) o;
		return x == vec3i.x && y == vec3i.y && z == vec3i.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}
