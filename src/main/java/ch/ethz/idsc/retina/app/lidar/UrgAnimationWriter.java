// code by jph
package ch.ethz.idsc.retina.app.lidar;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JLabel;

import ch.ethz.idsc.retina.dev.urg04lx.Urg04lxListener;
import ch.ethz.idsc.retina.dev.urg04lx.Urg04lxRender;
import ch.ethz.idsc.tensor.io.AnimationWriter;

public class UrgAnimationWriter implements Urg04lxListener {
  private static final JLabel OBSERVER = new JLabel();
  private final AnimationWriter animationWriter;
  private final Dimension dimension;
  private final BufferedImage image;
  private int frames = 0;
  private final Urg04lxRender urg04lxRender = new Urg04lxRender();

  public UrgAnimationWriter(File file, int period, Dimension dimension) throws Exception {
    animationWriter = AnimationWriter.of(file, period);
    this.dimension = dimension;
    image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
    urg04lxRender.setZoom(-3);
  }

  @Override
  public void urg(String line) {
    urg04lxRender.setLine(line);
    urg04lxRender.render((Graphics2D) image.getGraphics(), dimension);
    // if (60 < frames && frames < 240) // FIXME not final
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

  // @Override
  public void close() throws Exception {
    animationWriter.close();
    System.out.println("closed gif");
  }
}
