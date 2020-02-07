package dev.fiki.forgehax.main.util.typeconverter.types;

import com.google.common.base.MoreObjects;
import dev.fiki.forgehax.main.util.key.BindingHelper;
import dev.fiki.forgehax.main.util.typeconverter.TypeConverter;
import net.minecraft.client.util.InputMappings;

public class InputType extends TypeConverter<InputMappings.Input> {
  @Override
  public String label() {
    return "keybinding";
  }

  @Override
  public Class<InputMappings.Input> type() {
    return InputMappings.Input.class;
  }

  @Override
  public InputMappings.Input parse(String value) {
    return "none".equalsIgnoreCase(value)
        ? InputMappings.INPUT_INVALID
        : BindingHelper.getInputByName(value.toLowerCase());
  }

  @Override
  public String convert(InputMappings.Input value) {
    return MoreObjects.firstNonNull(value, InputMappings.INPUT_INVALID).getTranslationKey();
  }
}
