package dev.fiki.forgehax.main.util.cmd.settings;

import dev.fiki.forgehax.main.util.cmd.AbstractSetting;
import dev.fiki.forgehax.main.util.cmd.IParentCommand;
import dev.fiki.forgehax.main.util.cmd.flag.EnumFlag;
import dev.fiki.forgehax.main.util.typeconverter.IConverter;
import dev.fiki.forgehax.main.util.typeconverter.TypeConverters;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class LongSetting extends AbstractSetting<Long> {
  @Builder
  public LongSetting(IParentCommand parent,
      String name, @Singular Set<String> aliases, String description,
      @Singular Set<EnumFlag> flags, @Singular List<ISettingValueChanged<Long>> changedListeners,
      @NonNull Long defaultTo, Long min, Long max) {
    super(parent, name, aliases, description, flags, defaultTo, min, max);
    addListeners(ISettingValueChanged.class, changedListeners);
  }

  @Override
  public IConverter<Long> getConverter() {
    return TypeConverters.LONG;
  }
}
