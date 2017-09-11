// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx.app;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxEvent;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxEventListener;
import ch.ethz.idsc.tensor.io.AnimationWriter;

public class Urg04lxAnimationWriter implements Urg04lxEventListener {
  private final AnimationWriter animationWriter;
  private final Dimension dimension;
  private final BufferedImage image;
  private int frames = 0;
  private final Urg04lxRender urg04lxRender = new Urg04lxRender();

  public Urg04lxAnimationWriter(File file, int period, Dimension dimension) throws Exception {
    animationWriter = AnimationWriter.of(file, period);
    this.dimension = dimension;
    image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
    urg04lxRender.setZoom(-3);
  }

  @Override
  public void range(Urg04lxEvent urg04lxEvent) {
    urg04lxRender.setEvent(urg04lxEvent);
    urg04lxRender.render((Graphics2D) image.getGraphics(), dimension);
    try {
      animationWriter.append(image);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    ++frames;
  }

  public int frameCount() {
    return frames;
  }

  public void close() throws Exception {
    animationWriter.close();
    System.out.println("closed gif");
  }
}