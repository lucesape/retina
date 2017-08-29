// code by jph
package ch.ethz.idsc.retina.dev.urg04lxug01;

import ch.ethz.idsc.retina.util.RealtimeSleeper;

/** slows down playback of urg04lx recording to factor of real-time */
public class Urg04lxRealtimeListener implements Urg04lxListener {
  private static final long MILLI_TO_NANO = 1_000_000L;
  // ---
  private final RealtimeSleeper realtimeSleeper;

  public Urg04lxRealtimeListener(double factor) {
    realtimeSleeper = new RealtimeSleeper(factor);
  }

  @Override
  public void urg(String line) {
    int index = line.indexOf('{');
    long timestamp_ms = Long.parseLong(line.substring(3, index));
    // System.out.println(timestamp_ms);
    realtimeSleeper.now(timestamp_ms * MILLI_TO_NANO);
  }
}
