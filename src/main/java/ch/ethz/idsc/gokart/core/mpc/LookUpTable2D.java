// code by mheim
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import de.lmu.ifi.dbs.elki.utilities.exceptions.NotImplementedException;

//TODO: switch the whole thing to Tensor variables (this will not change any interactions)
//TODO: document this properly (to be done after the whole thing works)
public class LookUpTable2D {
  private static final float TOLERANCE = 0.001f;
  // ---
  final float table[][];
  private final int firstDimN;
  private final int secondDimN;
  private final float firstDimMin;
  private final float firstDimMax;
  private final float secondDimMin;
  private final float secondDimMax;
  private final Unit firstDimUnit;
  private final Unit secondDimUnit;
  private final Unit outputUnit;
  private LookupFunction originalFunction = null;

  public LookUpTable2D(BufferedReader csvReader) throws IOException {
    String line;
    // read dimensions
    firstDimN = Integer.parseInt(csvReader.readLine());
    secondDimN = Integer.parseInt(csvReader.readLine());
    table = new float[firstDimN][secondDimN];
    line = csvReader.readLine();
    // read limits
    String[] firstLimits = line.split(",");
    firstDimMin = Float.parseFloat(firstLimits[0]);
    firstDimMax = Float.parseFloat(firstLimits[1]);
    line = csvReader.readLine();
    String[] secondLimits = line.split(",");
    secondDimMin = Float.parseFloat(secondLimits[0]);
    secondDimMax = Float.parseFloat(secondLimits[1]);
    // read units
    firstDimUnit = Unit.of(csvReader.readLine());
    secondDimUnit = Unit.of(csvReader.readLine());
    outputUnit = Unit.of(csvReader.readLine());
    for (int i1 = 0; i1 < firstDimN; i1++) {
      line = csvReader.readLine();
      String[] linevals = line.split(",");
      for (int i2 = 0; i2 < secondDimN; i2++) {
        table[i1][i2] = Float.parseFloat(linevals[i2]);
      }
    }
  }

  public void saveTable(BufferedWriter csvWriter) throws IOException {
    // read dimensions
    csvWriter.write(firstDimN + "\n");
    csvWriter.write(secondDimN + "\n");
    csvWriter.write(firstDimMin + "," + firstDimMax + "\n");
    csvWriter.write(secondDimMin + "," + secondDimMax + "\n");
    // read units
    csvWriter.write(firstDimUnit + "\n");
    csvWriter.write(secondDimUnit + "\n");
    csvWriter.write(outputUnit + "\n");
    for (int i1 = 0; i1 < firstDimN; i1++) {
      String[] linevals = new String[secondDimN];
      for (int i2 = 0; i2 < secondDimN; ++i2)
        linevals[i2] = String.valueOf(table[i1][i2]);
      csvWriter.write(String.join(",", linevals) + "\n");
    }
  }

  public LookUpTable2D(float table[][], float firstDimMin, float firstDimMax, float secondDimMin, float secondDimMax, Unit firstDimUnit, Unit secondDimUnit,
      Unit outputUnit) {
    this.table = table;
    this.firstDimN = table.length;
    this.secondDimN = table[0].length;
    this.firstDimMin = firstDimMin;
    this.firstDimMax = firstDimMax;
    this.secondDimMin = secondDimMin;
    this.secondDimMax = secondDimMax;
    this.firstDimUnit = firstDimUnit;
    this.secondDimUnit = secondDimUnit;
    this.outputUnit = outputUnit;
  }

  public LookUpTable2D(float table[][], Scalar firstDimMin, Scalar firstDimMax, Scalar secondDimMin, Scalar secondDimMax, Unit firstDimUnit, Unit secondDimUnit,
      Unit outputUnit) {
    this.table = table;
    this.firstDimN = table.length;
    this.secondDimN = table[0].length;
    this.firstDimMin = firstDimMin.number().floatValue();
    this.firstDimMax = firstDimMax.number().floatValue();
    this.secondDimMin = secondDimMin.number().floatValue();
    this.secondDimMax = secondDimMax.number().floatValue();
    this.firstDimUnit = firstDimUnit;
    this.secondDimUnit = secondDimUnit;
    this.outputUnit = outputUnit;
  }

  static interface LookupFunction {
    Scalar getValue(Scalar firstValue, Scalar secondValue);
  }

  public LookUpTable2D(LookupFunction function, int firstDimN, int secondDimN, Scalar firstDimMin, Scalar firstDimMax, Scalar secondDimMin, Scalar secondDimMax,
      Unit firstDimUnit, Unit secondDimUnit, Unit outputUnit) {
    this.originalFunction = function;
    this.firstDimN = firstDimN;
    this.secondDimN = secondDimN;
    this.firstDimMin = firstDimMin.number().floatValue();
    this.firstDimMax = firstDimMax.number().floatValue();
    this.secondDimMin = secondDimMin.number().floatValue();
    this.secondDimMax = secondDimMax.number().floatValue();
    this.firstDimUnit = firstDimUnit;
    this.secondDimUnit = secondDimUnit;
    table = new float[firstDimN][secondDimN];
    for (int i1 = 0; i1 < firstDimN; i1++) {
      for (int i2 = 0; i2 < secondDimN; i2++) {
        float firstValuef = this.firstDimMin + (this.firstDimMax - this.firstDimMin) * i1 / (firstDimN - 1);
        Scalar firstValue = Quantity.of(//
            firstValuef, //
            firstDimUnit);
        float secondValuef = this.secondDimMin + (this.secondDimMax - this.secondDimMin) * i2 / (secondDimN - 1);
        Scalar secondValue = Quantity.of(// ,
            secondValuef, secondDimUnit);
        table[i1][i2] = function.getValue(firstValue, secondValue).number().floatValue();
      }
    }
    this.outputUnit = outputUnit;
  }

  /** get inverted lookup table target specifies which of the arguments gets to be
   * the the output: function should be monotone */
  private float getFunctionValue(float x, float y) {
    if (originalFunction != null)
      return originalFunction.getValue(//
          Quantity.of(x, this.firstDimUnit), //
          Quantity.of(y, this.secondDimUnit)).number().floatValue();
    throw new NotImplementedException("not tested yet!");
    // return getValue(x, y);
  }

  public LookUpTable2D getInverseLookupTableBinarySearch( //
      int target, int firstDimN, int secondDimN, Scalar newDimMin, Scalar newDimMax) {
    float firstDimMinf;
    float firstDimMaxf;
    float secondDimMinf;
    float secondDimMaxf;
    if (target == 0) {
      firstDimMinf = newDimMin.number().floatValue();
      firstDimMaxf = newDimMax.number().floatValue();
      secondDimMinf = secondDimMin;
      secondDimMaxf = secondDimMax;
    } else //
    if (target == 1) {
      firstDimMinf = firstDimMin;
      firstDimMaxf = firstDimMax;
      secondDimMinf = newDimMin.number().floatValue();
      secondDimMaxf = newDimMax.number().floatValue();
    } else
      return null;
    // switch x and out
    float table[][] = new float[firstDimN][secondDimN];
    for (int i1 = 0; i1 < firstDimN; i1++) {
      for (int i2 = 0; i2 < secondDimN; i2++) {
        float firstValuef = firstDimMinf//
            + (firstDimMaxf - firstDimMinf) * i1 / (firstDimN - 1);
        float secondValuef = secondDimMinf//
            + (secondDimMaxf - secondDimMinf) * i2 / (secondDimN - 1);
        // find appropriate value
        // use approximative gradient descent
        float lower;
        float upper;
        float mid = 0;
        if (target == 0) {
          lower = firstDimMin;
          upper = firstDimMax;
          while (Math.abs(upper - lower) > TOLERANCE) {
            mid = (lower + upper) / 2.0f;
            final float midValue = getFunctionValue(mid, secondValuef);
            if (midValue > firstValuef)
              upper = mid;
            else
              lower = mid;
          }
        } else //
        if (target == 1) {
          lower = secondDimMin;
          upper = secondDimMax;
          while (Math.abs(upper - lower) > TOLERANCE) {
            mid = (lower + upper) / 2.0f;
            final float midValue = getFunctionValue(firstValuef, mid);
            if (midValue > secondValuef)
              upper = mid;
            else
              lower = mid;
          }
        }
        table[i1][i2] = mid;
      }
    }
    if (target == 0)
      return new LookUpTable2D( //
          table, //
          firstDimMinf, //
          firstDimMaxf, //
          secondDimMinf, //
          secondDimMaxf, //
          outputUnit, //
          secondDimUnit, //
          firstDimUnit);
    if (target == 1)
      return new LookUpTable2D(//
          table, //
          firstDimMinf, //
          firstDimMaxf, //
          secondDimMinf, //
          secondDimMaxf, //
          firstDimUnit, //
          outputUnit, //
          secondDimUnit);
    return null;
  }

  private float getLookupValue(float x, float y) {
    float posx = (x - firstDimMin) / (firstDimMax - firstDimMin) * (firstDimN - 1);
    float posy = (y - secondDimMin) / (secondDimMax - secondDimMin) * (secondDimN - 1);
    if (posx < 0)
      posx = 0;
    if (posx > firstDimN - 1)
      posx = firstDimN - 1;
    if (posy < 0)
      posy = 0;
    if (posy > secondDimN - 1)
      posy = secondDimN - 1;
    int firstFrom = (int) Math.floor(posx);
    int firstTo = (int) Math.ceil(posx);
    int secondFrom = (int) Math.floor(posy);
    int secondTo = (int) Math.ceil(posy);
    float firstProg = (posx - firstFrom);
    float secondProg = (posy - secondFrom);
    // interpolate
    return //
    (1 - firstProg) * (1 - secondProg) * table[firstFrom][secondFrom]// 1
        + firstProg * (1 - secondProg) * table[firstTo][secondFrom]// 2
        + (1 - firstProg) * secondProg * table[firstFrom][secondTo]// 3
        + firstProg * secondProg * table[firstTo][secondTo];// 4
  }

  public Scalar lookup(Scalar x, Scalar y) {
    float fx = x.number().floatValue();
    float fy = y.number().floatValue();
    return Quantity.of( //
        getLookupValue(fx, fy), outputUnit);
  }

  /** delivers the extremal values in the specified direction
   * @param dimension the dimension along which the extremal are to be found
   * @param otherValue the value that is set at the other dimension
   * @return a Tensor containing the minimum and maximum value along the dimension */
  public Tensor getExtremalValues(int dimension, Scalar otherValue) {
    if (dimension == 0)
      return Tensors.of( //
          lookup(Quantity.of(firstDimMin, firstDimUnit), otherValue), //
          lookup(Quantity.of(firstDimMax, firstDimUnit), otherValue));
    if (dimension == 1)
      return Tensors.of(//
          lookup(otherValue, Quantity.of(secondDimMin, secondDimUnit)), //
          lookup(otherValue, Quantity.of(secondDimMax, secondDimUnit)));
    return null;
  }
}
