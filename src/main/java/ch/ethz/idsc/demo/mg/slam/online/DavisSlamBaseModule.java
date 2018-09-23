// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.gokart.core.pure.SlamCurvePurePursuitModule;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/** runs the SLAM algorithm and a pure pursuit module which gets a lookAhead point in the go kart frame
 * from the SLAM algorithm */
public class DavisSlamBaseModule extends AbstractClockedModule {
  private final SlamCurvePurePursuitModule slamCurvePurePursuitModule;
  private final OnlineSlamWrap onlineSlamWrap;

  DavisSlamBaseModule(SlamAlgoConfig slamAlgoConfig) {
    SlamCoreConfig.GLOBAL.slamAlgoConfig = slamAlgoConfig;
    onlineSlamWrap = new OnlineSlamWrap(SlamCoreConfig.GLOBAL);
    slamCurvePurePursuitModule = new SlamCurvePurePursuitModule();
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    onlineSlamWrap.start();
    // ---
    slamCurvePurePursuitModule.launch();
  }

  @Override // from AbstractModule
  protected void last() {
    onlineSlamWrap.stop();
    // ---
    slamCurvePurePursuitModule.terminate();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    Optional<Tensor> curve = onlineSlamWrap.getSlamCurveContainer().getCurve();
    slamCurvePurePursuitModule.setCurve(curve);
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return Quantity.of(0.1, SI.SECOND);
  }

  public static void standalone() throws Exception {
    DavisSlamBaseModule davisSlamBaseModule = new DavisSlamBaseModule(SlamCoreConfig.GLOBAL.slamAlgoConfig);
    davisSlamBaseModule.launch();
  }
}