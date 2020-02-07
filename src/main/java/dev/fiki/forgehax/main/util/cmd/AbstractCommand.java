package dev.fiki.forgehax.main.util.cmd;

import com.google.common.collect.ImmutableSet;
import dev.fiki.forgehax.main.util.cmd.flag.EnumFlag;
import dev.fiki.forgehax.main.util.cmd.listener.IUpdateConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;

@Getter
public abstract class AbstractCommand implements ICommand {
  @Setter
  private IParentCommand parent;

  private final String name;
  private final Set<String> aliases;

  private final String description;

  private final Set<EnumFlag> flags;

  public AbstractCommand(IParentCommand parent,
      @NonNull String name, @NonNull Collection<String> aliases, @NonNull String description,
      @NonNull Collection<EnumFlag> flags) {
    this.name = name;
    this.aliases = ImmutableSet.copyOf(aliases);
    this.description = description;

    EnumSet<EnumFlag> es = EnumSet.noneOf(EnumFlag.class);
    es.addAll(flags);
    this.flags = Collections.synchronizedSet(es);

    this.init();

    if(parent != null) {
      // all this command to the parent
      parent.addChild(this);
    }
  }

  protected void init() { }

  protected void callUpdateListeners() {
    getListeners(IUpdateConfiguration.class).forEach(l -> l.onUpdate(this));
  }

  @Override
  public boolean addFlag(EnumFlag flag) {
    return flags.add(flag);
  }

  @Override
  public boolean deleteFlag(EnumFlag flag) {
    return flags.remove(flag);
  }

  @Override
  public boolean containsFlag(EnumFlag flag) {
    return flags.contains(flag);
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractCommand command = (AbstractCommand) o;
    return name.equalsIgnoreCase(command.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name.toLowerCase());
  }
}
