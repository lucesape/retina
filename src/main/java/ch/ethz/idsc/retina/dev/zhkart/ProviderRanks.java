// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

public enum ProviderRanks {
  ;
  private static Map<ProviderRank, Color> MAP = new EnumMap<>(ProviderRank.class);
  static {
    for (ProviderRank providerRank : ProviderRank.values())
      MAP.put(providerRank, Color.WHITE);
    // ---
    MAP.put(ProviderRank.EMERGENCY, new Color(255, 128, 128)); // red
    MAP.put(ProviderRank.PROTECTION, new Color(222, 195, 174)); // yellow/red
    MAP.put(ProviderRank.CALIBRATION, new Color(0, 255, 255)); // turquoise
    MAP.put(ProviderRank.MANUAL, new Color(128, 255, 128)); // green
    MAP.put(ProviderRank.TESTING, new Color(255, 255, 0)); // yellow
    MAP.put(ProviderRank.FALLBACK, new Color(192, 192, 192));
  }

  public static Color color(ProviderRank providerRank) {
    return MAP.get(providerRank);
  }
}