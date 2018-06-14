// code by jph
package ch.ethz.idsc.retina.dev.lidar;

public abstract class VelodyneSpacialProvider implements LidarSpacialProvider {
  protected int limit_lo = VelodyneStatics.DEFAULT_LIMIT_LO;

  /** quote from the user's manual, p.8: "the minimum return distance for the
   * HDL-32E is approximately 1 meter. ignore returns closer than this"
   * 
   * however, we find that in office conditions correct ranges below 1 meter are
   * provided
   * 
   * @param closest in [m] */
  public final void setLimitLo(double closest) {
    limit_lo = (int) (closest / VelodyneStatics.TO_METER);
  }
}
