// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.util.IntRange;

public class DavisTallyEvent {
  public final int first;
  private int last;
  public final int shift; // 2^shift
  public int[] bin = new int[1600]; // TODO magic const
  public int binLast = -1;
  public IntRange resetRange = new IntRange(0, 0);
  public IntRange imageRange = null;

  public DavisTallyEvent(int first, int shift) {
    this.first = first;
    this.shift = shift;
  }

  private int binIndex(int time) {
    time -= first;
    return time >> shift;
  }

  public void register(int time) {
    int index = binIndex(time);
    if (0 <= index && index < bin.length) { // TODO check negative!!!
      ++bin[index];
      binLast = Math.max(binLast, index);
    }
  }

  public void setResetBlock(int beg, int end) {
    try {
      resetRange = new IntRange(binIndex(beg), binIndex(end));
    } catch (Exception exception) {
      System.err.println("timing !");
    }
  }

  public void setImageBlock(int beg, int end) {
    try {
      imageRange = new IntRange(binIndex(beg), binIndex(end));
    } catch (Exception exception) {
      System.err.println("timing !");
    }
  }

  public void setMax(int time) {
    last = time;
    int index = binIndex(time);
    if (0 <= index && index < bin.length) { // TODO check negative!!!
      binLast = index;
    }
  }

  public int getDurationUs() {
    return last - first;
  }
}
