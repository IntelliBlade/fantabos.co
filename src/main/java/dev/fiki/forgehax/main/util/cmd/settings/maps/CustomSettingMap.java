package dev.fiki.forgehax.main.util.cmd.settings.maps;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.fiki.forgehax.main.util.cmd.AbstractSettingMap;
import dev.fiki.forgehax.main.util.cmd.IParentCommand;
import dev.fiki.forgehax.main.util.cmd.flag.EnumFlag;
import dev.fiki.forgehax.main.util.serialization.IJsonSerializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class CustomSettingMap<K extends IJsonSerializable, V extends IJsonSerializable>
    extends AbstractSettingMap<K, V, Map<K, V>> {
  @Getter(AccessLevel.PROTECTED)
  private final Supplier<K> keySupplier;
  @Getter(AccessLevel.PROTECTED)
  private final Supplier<V> valueSupplier;

  @Builder
  public CustomSettingMap(IParentCommand parent,
      String name, Collection<String> aliases, String description,
      Collection<EnumFlag> flags,
      Supplier<Map<K, V>> supplier,
      @NonNull Supplier<K> keySupplier, @NonNull Supplier<V> valueSupplier) {
    super(parent, name, aliases, description, flags, supplier);
    this.keySupplier = keySupplier;
    this.valueSupplier = valueSupplier;
  }

  @Override
  public JsonElement serialize() {
    JsonArray array = new JsonArray();

    for (Map.Entry<K, V> entry : this.entrySet()) {
      JsonObject object = new JsonObject();
      object.add("key", entry.getKey().serialize());
      object.add("value", entry.getValue().serialize());
    }

    return array;
  }

  @Override
  public void deserialize(JsonElement json) {
    if (!json.isJsonArray()) {
      throw new IllegalArgumentException("expected JsonArray, got " + json.getClass().getSimpleName());
    }

    JsonArray array = json.getAsJsonArray();

    for (JsonElement element : array) {
      JsonObject object = element.getAsJsonObject();
      K key = keySupplier.get();
      key.deserialize(object.get("key"));

      V value = valueSupplier.get();
      value.deserialize(object.get("value"));

      this.wrapping.put(key, value);
    }
  }
}
