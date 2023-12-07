package org.quiltmc.config.api.serializer;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.MarshallingUtils;
import org.quiltmc.config.api.Serializer;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.values.CompoundConfigValue;
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.api.values.ValueTreeNode;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TomlSerializer implements Serializer {
	public static final TomlSerializer INSTANCE = new TomlSerializer();
	private final ConfigParser<CommentedConfig> parser = new TomlParser();
	private final ConfigWriter writer = new TomlWriter();

	@Override
	public String getFileExtension() {
		return "toml";
	}

	@Override
	public void serialize(Config config, OutputStream to) {
		this.writer.write(write(createCommentedConfig(), config.nodes()), to);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void deserialize(Config config, InputStream from) {
		CommentedConfig read = this.parser.parse(from);

		for (TrackedValue<?> trackedValue : config.values()) {
			String name = trackedValue.key().toString();
			if (trackedValue.hasMetadata(SerializedName.TYPE)) {
				name = trackedValue.metadata(SerializedName.TYPE).getName();
			}

			if (read.contains(name)) {
				((TrackedValue) trackedValue).setValue(MarshallingUtils.coerce(read.get(name), trackedValue.getDefaultValue(), (CommentedConfig c, MarshallingUtils.MapEntryConsumer entryConsumer) ->
					c.entrySet().forEach(e -> entryConsumer.put(e.getKey(), e.getValue()))), false);
			}
		}
	}

	private static List<Object> convertList(List<?> list) {
		List<Object> result = new ArrayList<>(list.size());

		for (Object value : list) {
			result.add(convertAny(value));
		}

		return result;
	}

	private static UnmodifiableCommentedConfig convertMap(ValueMap<?> map) {
		CommentedConfig result = createCommentedConfig();

		for (Map.Entry<String, ?> entry : map.entrySet()) {
			result.add(entry.getKey(), convertAny(entry.getValue()));
		}

		return result;
	}

	private static Object convertAny(Object value) {
		if (value instanceof ValueMap) {
			return convertMap((ValueMap<?>) value);
		} else if (value instanceof ValueList) {
			return convertList((ValueList<?>) value);
		} else if (value instanceof ConfigSerializableObject) {
			return convertAny(((ConfigSerializableObject<?>) value).getRepresentation());
		} else {
			return value;
		}
	}

	private static CommentedConfig write(CommentedConfig config, Iterable<ValueTreeNode> nodes) {
		for (ValueTreeNode node : nodes) {
			List<String> comments = new ArrayList<>();

			if (node.hasMetadata(Comment.TYPE)) {
				for (String string : node.metadata(Comment.TYPE)) {
					comments.add(string);
				}
			}

			if (node instanceof TrackedValue<?>) {
				TrackedValue<?> value = (TrackedValue<?>) node;
				Object defaultValue = value.getDefaultValue();

				if (defaultValue.getClass().isEnum()) {
					StringBuilder options = new StringBuilder("options: ");
					Object[] enumConstants = defaultValue.getClass().getEnumConstants();

					for (int i = 0, enumConstantsLength = enumConstants.length; i < enumConstantsLength; i++) {
						Object o = enumConstants[i];

						options.append(o);

						if (i < enumConstantsLength - 1) {
							options.append(", ");
						}
					}

					comments.add(options.toString());
				}

				for (Constraint<?> constraint : value.constraints()) {
					comments.add(constraint.getRepresentation());
				}

				if (!(defaultValue instanceof CompoundConfigValue<?>)) {
					comments.add("default: " + defaultValue);
				}

				String name = value.key().toString();
				if (value.hasMetadata(SerializedName.TYPE)) {
					name = value.metadata(SerializedName.TYPE).getName();
				}

				config.add(name, convertAny(value.getRealValue()));
			} else {
				write(config, ((ValueTreeNode.Section) node));
			}

			if (!comments.isEmpty()) {
				config.setComment(node.key().toString(), " " + String.join("\n ", comments));
			}
		}

		return config;
	}

	private static CommentedConfig createCommentedConfig() {
		return InMemoryCommentedFormat.defaultInstance().createConfig(LinkedHashMap::new);
	}
}
