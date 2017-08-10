// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dvs.app.DefaultDavisDisplay;
import ch.ethz.idsc.retina.dvs.io.aps.ApsStandaloneClient;

enum AedatImageClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    DefaultDavisDisplay davisImageDisplay = new DefaultDavisDisplay();
    ApsStandaloneClient asc = new ApsStandaloneClient(davisDecoder);
    asc.addListener(davisImageDisplay.apsRenderer);
    asc.start(); // TODO at the moment this is a blocking call !?
    System.out.println("here");
  }
}