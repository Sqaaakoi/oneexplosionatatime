package sqaaakoi.minecraft_mods.oneexplosionatatime;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer {
  public static final String ID = "oneexplosionatatime";
  public static final Logger LOG = LogManager.getLogger(ID);

  @Override
  public void onInitialize() {
    LOG.debug("Loaded mod {}", ID);
  }
}
