package de.adrodoc55.common.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public abstract class CollectionUtils {

  @SafeVarargs
  public static <E> ArrayList<E> newArrayList(E... elements) {
    ArrayList<E> arrayList;
    if (elements == null) {
      arrayList = new ArrayList<E>(1);
      arrayList.add(null);
    } else {
      arrayList = new ArrayList<E>(elements.length);
      for (E e : elements) {
        arrayList.add(e);
      }
    }
    return arrayList;
  }

  public static <E> ArrayList<E> newArrayList(Iterable<E> elements) {
    ArrayList<E> arrayList = new ArrayList<E>();
    for (E e : elements) {
      arrayList.add(e);
    }
    return arrayList;
  }

  public static <E> void reverse(E[] elements) {
    @SuppressWarnings("unchecked")
    E[] temp = (E[]) new Object[elements.length];
    for (int x = 0; x < elements.length; x++) {
      temp[x] = elements[x];
    }
    for (int x = 0; x < temp.length; x++) {
      elements[x] = temp[temp.length - 1 - x];
    }
  }

  // public static void main(String[] args) {
  // String[] empty = {};
  // empty = removeNullValues(empty);
  // System.out.println(Arrays.toString(empty));
  //
  // String[] filled = { "1", "2", "3" };
  // filled = removeNullValues(filled);
  // System.out.println(Arrays.toString(filled));
  //
  // String[] onlyNull = { null };
  // onlyNull = removeNullValues(onlyNull);
  // System.out.println(Arrays.toString(onlyNull));
  //
  // String[] middle = { "1", null, "3" };
  // middle = removeNullValues(middle);
  // System.out.println(Arrays.toString(middle));
  // }

  public static <E> E[] removeNullValues(E[] elements) {
    int nullCount = 0;
    for (E e : elements) {
      if (e == null) {
        nullCount++;
      }
    }
    Class<?> clazz = elements.getClass().getComponentType();
    @SuppressWarnings("unchecked")
    E[] result = (E[]) Array.newInstance(clazz, elements.length - nullCount);
    // E[] result = (E[]) new Object[elements.length - nullCount];
    int y = 0;
    for (int x = 0; x < elements.length; x++) {
      if (elements[x] != null) {
        result[y++] = elements[x];
      }
    }
    return result;
  }

  /**
   * Iterates through this iterable transforming each entry into a new value using the transform
   * closure returning a list of transformed values.
   * 
   * @param iterable
   * @param transform - the closure used to transform each item of the iterable.
   * @return a list of transformed values.
   */
  public static <P, R> List<R> collect(Iterable<? extends P> iterable, Closure<P, R> transform) {
    List<R> list = new ArrayList<R>();
    for (P p : iterable) {
      list.add(transform.call(p));
    }
    return list;
  }

  /**
   * Finds the first value matching the closure condition.
   * 
   * @param iterable
   * @param closure - a closure condition.
   * @return the first matching element or null if there is none.
   */
  public static <P> P find(Iterable<? extends P> iterable, Closure<P, Boolean> closure) {
    for (P p : iterable) {
      if (closure.call(p)) {
        return p;
      }
    }
    return null;
  }

  /**
   * Finds all values matching the closure condition.
   * 
   * @param iterable
   * @param closure - a closure condition.
   * @return a list of matching values.
   */
  public static <P> List<P> findAll(Iterable<? extends P> iterable, Closure<P, Boolean> closure) {
    List<P> list = new ArrayList<P>();
    for (P p : iterable) {
      if (closure.call(p)) {
        list.add(p);
      }
    }
    return list;
  }

}
