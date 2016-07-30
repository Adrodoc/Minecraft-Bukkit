package de.adrodoc55.common;

import java.text.DecimalFormat;

public abstract class CommonUtils {

  public static String join(String on, Object... args) {
    StringBuilder sb = new StringBuilder();
    boolean firstElement = true;
    for (Object arg : args) {
      if (!firstElement) {
        sb.append(on);
      }
      // String string;
      // if (arg instanceof Iterable<?>) {
      // string = join(on, args);
      // } else {
      // string = String.valueOf(arg);
      // }
      sb.append(String.valueOf(arg));
      firstElement = false;
    }
    return sb.toString();
  }

  public static String join(String on, Iterable<?> args) {
    StringBuilder sb = new StringBuilder();
    boolean firstElement = true;
    for (Object arg : args) {
      if (!firstElement) {
        sb.append(on);
      }
      sb.append(String.valueOf(arg));
      firstElement = false;
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    double d = 10.0000000000001;
    System.out.println(d);
    System.out.println("Double:" + d);
    System.out.println("Double:" + String.valueOf(d));
    System.out.println(String.format("Double: %f", d));
    System.out.println(String.format("Double: %s", d));
    System.out.println("Double: " + Double.toString(d));
    DecimalFormat df = new DecimalFormat("0");
    df.setMaximumFractionDigits(340);
    System.out.println("Double: " + df.format(d));
  }

  private static DecimalFormat DF;

  private static DecimalFormat getFormat() {
    if (DF == null) {
      DF = new DecimalFormat("0");
      DF.setMaximumFractionDigits(340);
    }
    return DF;
  }

  public static String doubleToString(double d) {
    return getFormat().format(d);
  }
}
