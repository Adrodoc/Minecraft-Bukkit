package de.adrodoc55.common.collections;

/**
 * Eine Closure als funktionales Interface für Lambda Ausdrücke.
 * 
 * @author Adrodoc55
 *
 * @param <P> der Parametertyp
 * @param <R> der Rückgabetyp
 */
public interface Closure<P, R> {

	/**
	 * 
	 * @param p der Parameter
	 * @return r der Rückgabewert
	 */
	public abstract R call(P p);

}
