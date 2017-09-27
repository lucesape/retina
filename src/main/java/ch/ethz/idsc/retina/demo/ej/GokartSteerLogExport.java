// code by jph
package ch.ethz.idsc.retina.demo.ej;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.io.UserHome;
import ch.ethz.idsc.tensor.StringScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ArrayQ;
import ch.ethz.idsc.tensor.io.Export;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

enum GokartSteerLogExport {
  ;
  public static void main(String[] args) throws IOException {
    File file = new File("/home/jelavice", "20170927T192959_868e105d.lcm.00"); // put hte filename here
    Log log = new Log(file.toString(), "r");
    long countGet = 0;
    long countPut = 0;
    Tensor tableGet = Tensors.empty();
    Tensor tablePut = Tensors.empty();
    Tensor tablePutRef = Tensors.empty();
    tableGet.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("motAsp_CANInput"), StringScalar.of("gcpRelRckPos")));
    tablePut.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("torque")));
    tablePutRef.append(Tensors.of(StringScalar.of("time_us"), StringScalar.of("position")));
    Long tic = null;
    try {
      while (true) {
        Event event = log.readNext();
        if (Objects.isNull(tic))
          tic = event.utime;
        if (event.channel.equals("autobox.steer.get")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
          tableGet.append(Tensors.vector(event.utime - tic, steerGetEvent.motAsp_CANInput, steerGetEvent.getSteeringAngle()));
          // System.out.println(steerGetEvent.getSteeringAngle());
          ++countGet;
        } else //
        if (event.channel.equals("autobox.steer.put")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          SteerPutEvent steerPutEvent = SteerPutEvent.from(byteBuffer);
          tablePut.append(Tensors.vector(event.utime - tic, steerPutEvent.getTorque()));
          ++countPut;
        } else //
        if (event.channel.equals("myChannel")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          double value = byteBuffer.getDouble();
          tablePutRef.append(Tensors.vector(event.utime - tic, value));
        }
      }
    } catch (Exception exception) {
      // ---
    }
    System.out.println("" + countGet);
    System.out.println("" + countPut);
    GlobalAssert.that(ArrayQ.of(tableGet));
    GlobalAssert.that(ArrayQ.of(tablePut));
    GlobalAssert.that(ArrayQ.of(tablePutRef));
    Export.of(UserHome.file("sysid_get.csv"), tableGet);
    Export.of(UserHome.file("sysid_put.csv"), tablePut);
    Export.of(UserHome.file("sysid_putRef.csv"), tablePutRef);
  }
}
