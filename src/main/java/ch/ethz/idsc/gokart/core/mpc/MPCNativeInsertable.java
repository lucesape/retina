// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

// TODO JPH similar to {@link DataEvent} -> unify
public interface MPCNativeInsertable {
  void insert(ByteBuffer byteBuffer);

  int length();
}
