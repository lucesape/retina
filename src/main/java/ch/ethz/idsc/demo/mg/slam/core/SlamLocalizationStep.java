// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

/** directly sets the pose estimate of the SLAM algorithm. Can be used if an accurate external pose is available, e.g. through lidar */
/* package */ class SlamLocalizationStep extends PeriodicSlamStep {
  private final GokartPoseInterface gokartPoseInterface;

  protected SlamLocalizationStep(SlamCoreContainer slamCoreContainer, GokartPoseInterface gokartPoseInterface) {
    super(slamCoreContainer, SlamDvsConfig.eventCamera.slamCoreConfig.localizationUpdateRate);
    this.gokartPoseInterface = gokartPoseInterface;
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    slamCoreContainer.setPose(gokartPoseInterface.getPose());
  }
}
