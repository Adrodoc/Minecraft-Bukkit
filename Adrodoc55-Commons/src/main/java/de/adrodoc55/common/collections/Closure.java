package de.adrodoc55.common.collections;

/**
 * Eine Closure als funktionales Interface f�r Lambda Ausdr�cke.
 * 
 * @author Adrodoc55
 *
 * @param <P> der Parametertyp
 * @param <R> der R�ckgabetyp
 */
public interface Closure<P, R> {

	/**
	 * 
	 * @param p der Parameter
	 * @return r der R�ckgabewert
	 */
	public abstract R call(P p);

}
