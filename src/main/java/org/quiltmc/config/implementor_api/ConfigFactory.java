/*
 * Copyright 2023-2024 QuiltMC
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

package org.quiltmc.config.implementor_api;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.impl.ConfigImpl;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class ConfigFactory {
	private ConfigFactory() {}

	/**
	 * Creates and registers a config file
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the configs id
	 * @param path additional path elements to include as part of this configs file, e.g.
	 *             if the path is empty, the config file might be ".minecraft/config/example_mod/id.toml"
	 *             if the path is "client/gui", the config file might be ".minecraft/config/example_mod/client/gui/id.toml"
	 * @param creators any number of {@link Config.Creator}s that can be used to configure the resulting config
	 */
	public static Config create(ConfigEnvironment environment, String family, String id, Path path, Config.Creator... creators) {
		return ConfigImpl.create(environment, family, id, path, creators);
	}

	/**
	 * Creates and registers a config file
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the configs id
	 * @param creators any number of {@link Config.Creator}s that can be used to configure the resulting config
	 */
	public static Config create(ConfigEnvironment environment, String family, String id, Config.Creator... creators) {
		return create(environment, family, id, Paths.get(""), creators);
	}

	/**
	 * Creates and registers a config with fields derived from the fields of the passed class
	 *
	 * <p>The passed class should have the following characteristics:
	 * <ul>
	 *     <li>Has a public no-argument constructor</li>
	 *     <li>Each non-public static non-transient field should be final, not null, and be one of the following types:
	 *     <ul>
	 *         <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *         <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types or a {@link org.quiltmc.config.api.values.ConfigSerializableObject})</li>
	 *         <li>An object whose class follows these rules</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * <p>Certain annotations can also be used on fields of this class to attach metadata to them. The {@link org.quiltmc.config.api.annotations.Comment}
	 * annotation is one such annotation that is provided by default, but additional {@link ConfigFieldAnnotationProcessor}s
	 * can be registered with {@link ConfigFieldAnnotationProcessor#register(Class, ConfigFieldAnnotationProcessor)}.
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the config's id
	 * @param path additional path elements to include as part of this configs file, e.g.
	 *             if the path is empty, the config file might be ".minecraft/config/example_mod/id.toml"
	 *             if the path is "client/gui", the config file might be ".minecraft/config/example_mod/client/gui/id.toml"
	 * @param before a {@link Config.Creator} that can be used to configure the resulting config further
	 * @param configCreatorClass a class as described above
	 * @param after a {@link Config.Creator} that can be used to configure the resulting config further
	 * @return a {@link ReflectiveConfig} of the type passed in.
	 */
	public static <C extends ReflectiveConfig> C create(ConfigEnvironment environment, String family, String id, Path path, Config.Creator before, Class<C> configCreatorClass, Config.Creator after) {
		return ConfigImpl.createReflective(environment, family, id, path, before, configCreatorClass, after);
	}

	/**
	 * Creates and registers a config with fields derived from the fields of the passed class
	 *
	 * <p>The passed class should have the following characteristics:
	 * <ul>
	 *     <li>Has a public no-argument constructor</li>
	 *     <li>Each non-public static non-transient field should be final, not null, and be one of the following types:
	 *     <ul>
	 *         <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *         <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types or a {@link org.quiltmc.config.api.values.ConfigSerializableObject})</li>
	 *         <li>An object whose class follows these rules</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * <p>Certain annotations can also be used on fields of this class to attach metadata to them. The {@link org.quiltmc.config.api.annotations.Comment}
	 * annotation is one such annotation that is provided by default, but additional {@link ConfigFieldAnnotationProcessor}s
	 * can be registered with {@link ConfigFieldAnnotationProcessor#register(Class, ConfigFieldAnnotationProcessor)}.
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the config's id
	 * @param path additional path elements to include as part of this configs file, e.g.
	 *             if the path is empty, the config file might be ".minecraft/config/example_mod/id.toml"
	 *             if the path is "client/gui", the config file might be ".minecraft/config/example_mod/client/gui/id.toml"
	 * @param before a {@link Config.Creator} that can be used to configure the resulting config further
	 * @param configCreatorClass a class as described above
	 * @return a {@link ReflectiveConfig} of the {@link Class} passed in.
	 */
	public static <C extends ReflectiveConfig> C create(ConfigEnvironment environment, String family, String id, Path path, Config.Creator before, Class<C> configCreatorClass) {
		return create(environment, family, id, path, before, configCreatorClass, builder -> {});
	}

	/**
	 * Creates and registers a config with fields derived from the fields of the passed class
	 *
	 * <p>The passed class should have the following characteristics:
	 * <ul>
	 *     <li>Has a public no-argument constructor</li>
	 *     <li>Each non-public static non-transient field should be final, not null, and be one of the following types:
	 *     <ul>
	 *         <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *         <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types or a {@link org.quiltmc.config.api.values.ConfigSerializableObject})</li>
	 *         <li>An object whose class follows these rules</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * <p>Certain annotations can also be used on fields of this class to attach metadata to them. The {@link org.quiltmc.config.api.annotations.Comment}
	 * annotation is one such annotation that is provided by default, but additional {@link ConfigFieldAnnotationProcessor}s
	 * can be registered with {@link ConfigFieldAnnotationProcessor#register(Class, ConfigFieldAnnotationProcessor)}.
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the configs id
	 * @param path additional path elements to include as part of this configs file, e.g.
	 *             if the path is empty, the config file might be ".minecraft/config/example_mod/id.toml"
	 *             if the path is "client/gui", the config file might be ".minecraft/config/example_mod/client/gui/id.toml"
	 * @param configCreatorClass a class as described above
	 * @param after a {@link Config.Creator} that can be used to configure the resulting config further
	 * @return a {@link ReflectiveConfig} of the {@link Class} passed in.
	 */
	public static <C extends ReflectiveConfig> C create(ConfigEnvironment environment, String family, String id, Path path, Class<C> configCreatorClass, Config.Creator after) {
		return create(environment, family, id, path, builder -> {}, configCreatorClass, after);
	}

	/**
	 * Creates and registers a config with fields derived from the fields of the passed class
	 *
	 * <p>The passed class should have the following characteristics:
	 * <ul>
	 *     <li>Has a public no-argument constructor</li>
	 *     <li>Each non-public static non-transient field should be final, not null, and be one of the following types:
	 *     <ul>
	 *         <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *         <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types or a {@link org.quiltmc.config.api.values.ConfigSerializableObject})</li>
	 *         <li>An object whose class follows these rules</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * <p>Certain annotations can also be used on fields of this class to attach metadata to them. The {@link org.quiltmc.config.api.annotations.Comment}
	 * annotation is one such annotation that is provided by default, but additional {@link ConfigFieldAnnotationProcessor}s
	 * can be registered with {@link ConfigFieldAnnotationProcessor#register(Class, ConfigFieldAnnotationProcessor)}.
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the config's id
	 * @param path additional path elements to include as part of this configs file, e.g.
	 *             if the path is empty, the config file might be ".minecraft/config/example_mod/id.toml"
	 *             if the path is "client/gui", the config file might be ".minecraft/config/example_mod/client/gui/id.toml"
	 * @param configCreatorClass a class as described above
	 * @return a {@link ReflectiveConfig} of the {@link Class} passed in.
	 */
	public static <C extends ReflectiveConfig> C create(ConfigEnvironment environment, String family, String id, Path path, Class<C> configCreatorClass) {
		return create(environment, family, id, path, builder -> {}, configCreatorClass, builder -> {});
	}

	/**
	 * Creates and registers a config with fields derived from the fields of the passed class
	 *
	 * <p>The passed class should have the following characteristics:
	 * <ul>
	 *     <li>Has a public no-argument constructor</li>
	 *     <li>Each non-public static non-transient field should be final, not null, and be one of the following types:
	 *     <ul>
	 *         <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *         <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types or a {@link org.quiltmc.config.api.values.ConfigSerializableObject})</li>
	 *         <li>An object whose class follows these rules</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * <p>Certain annotations can also be used on fields of this class to attach metadata to them. The {@link org.quiltmc.config.api.annotations.Comment}
	 * annotation is one such annotation that is provided by default, but additional {@link ConfigFieldAnnotationProcessor}s
	 * can be registered with {@link ConfigFieldAnnotationProcessor#register(Class, ConfigFieldAnnotationProcessor)}.
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the config's id
	 * @param before a {@link Config.Creator} that can be used to configure the resulting config further
	 * @param configCreatorClass a class as described above
	 * @param after a {@link Config.Creator} that can be used to configure the resulting config further
	 * @return a {@link ReflectiveConfig} of the {@link Class} passed in.
	 */
	public static <C extends ReflectiveConfig> C create(ConfigEnvironment environment, String family, String id, Config.Creator before, Class<C> configCreatorClass, Config.Creator after) {
		return create(environment, family, id, Paths.get(""), before, configCreatorClass, after);
	}

	/**
	 * Creates and registers a config with fields derived from the fields of the passed class
	 *
	 * <p>The passed class should have the following characteristics:
	 * <ul>
	 *     <li>Has a public no-argument constructor</li>
	 *     <li>Each non-public static non-transient field should be final, not null, and be one of the following types:
	 *     <ul>
	 *         <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *         <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types or a {@link org.quiltmc.config.api.values.ConfigSerializableObject})</li>
	 *         <li>An object whose class follows these rules</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * <p>Certain annotations can also be used on fields of this class to attach metadata to them. The {@link org.quiltmc.config.api.annotations.Comment}
	 * annotation is one such annotation that is provided by default, but additional {@link ConfigFieldAnnotationProcessor}s
	 * can be registered with {@link ConfigFieldAnnotationProcessor#register(Class, ConfigFieldAnnotationProcessor)}.
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the config's id
	 * @param before a {@link Config.Creator} that can be used to configure the resulting config further
	 * @param configCreatorClass a class as described above
	 * @return a {@link ReflectiveConfig} of the {@link Class} passed in.
	 */
	public static <C extends ReflectiveConfig> C create(ConfigEnvironment environment, String family, String id, Config.Creator before, Class<C> configCreatorClass) {
		return create(environment, family, id, Paths.get(""), before, configCreatorClass, builder -> {});
	}

	/**
	 * Creates and registers a config with fields derived from the fields of the passed class
	 *
	 * <p>The passed class should have the following characteristics:
	 * <ul>
	 *     <li>Has a public no-argument constructor</li>
	 *     <li>Each non-public static non-transient field should be final, not null, and be one of the following types:
	 *     <ul>
	 *         <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *         <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types or a {@link org.quiltmc.config.api.values.ConfigSerializableObject})</li>
	 *         <li>An object whose class follows these rules</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * <p>Certain annotations can also be used on fields of this class to attach metadata to them. The {@link org.quiltmc.config.api.annotations.Comment}
	 * annotation is one such annotation that is provided by default, but additional {@link ConfigFieldAnnotationProcessor}s
	 * can be registered with {@link ConfigFieldAnnotationProcessor#register(Class, ConfigFieldAnnotationProcessor)}.
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the config's id
	 * @param configCreatorClass a class as described above
	 * @param after a {@link Config.Creator} that can be used to configure the resulting config further
	 * @return a {@link ReflectiveConfig} of the {@link Class} passed in.
	 */
	public static <C extends ReflectiveConfig> C create(ConfigEnvironment environment, String family, String id, Class<C> configCreatorClass, Config.Creator after) {
		return create(environment, family, id, Paths.get(""), builder -> {}, configCreatorClass, after);
	}

	/**
	 * Creates and registers a config with fields derived from the fields of the passed class
	 *
	 * <p>The passed class should have the following characteristics:
	 * <ul>
	 *     <li>Has a public no-argument constructor</li>
	 *     <li>Each non-public static field should be final and be one of the following types:
	 *     <ul>
	 *     	   <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *     	   <li>A complex type (a {@link ValueList} or {@link ValueMap} of basic or complex types)</li>
	 *         <li>An object whose class follows these rules</li>
	 *     </ul></li>
	 * </ul>
	 *
	 * @param family the mod owning the resulting config file
	 * @param id the config's id
	 * @param configCreatorClass a class as described above
	 * @return a {@link ReflectiveConfig} of the {@link Class} passed in.
	 */
	public static <C extends ReflectiveConfig> C create(ConfigEnvironment environment, String family, String id, Class<C> configCreatorClass) {
		return create(environment, family, id, Paths.get(""), builder -> {}, configCreatorClass, builder -> {});
	}
}
