// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Clip;

public class GokartJoystickAdapter implements GokartJoystickInterface {
  private final Scalar steerLeft;
  private final Scalar breakStrength;
  private final Scalar ahead;
  private final Tensor pair;

  /** see {@link GokartJoystickInterface} for valid range of arguments
   * 
   * @param steerLeft real scalar
   * @param breakStrength
   * @param ahead real scalar
   * @param pair vector of length 2
   * @throws Exception if any argument is not in valid range */
  public GokartJoystickAdapter(Scalar steerLeft, Scalar breakStrength, Scalar ahead, Tensor pair) {
    Clip.absoluteOne().isInsideElseThrow(steerLeft);
    Clip.unit().isInsideElseThrow(breakStrength);
    Clip.absoluteOne().isInsideElseThrow(ahead);
    if (!pair.map(Clip.unit()).equals(pair))
      throw TensorRuntimeException.of(pair);
    // ---
    this.steerLeft = steerLeft;
    this.breakStrength = breakStrength;
    this.ahead = ahead;
    this.pair = pair.copy();
  }

  @Override // from GokartJoystickInterface
  public Scalar getSteerLeft() {
    return steerLeft;
  }

  @Override // from GokartJoystickInterface
  public Scalar getBreakStrength() {
    return breakStrength;
  }

  @Override // from GokartJoystickInterface
  public Scalar getAheadAverage() {
    return ahead;
  }

  @Override // from GokartJoystickInterface
  public Tensor getAheadPair_Unit() {
    return pair;
  }
}
