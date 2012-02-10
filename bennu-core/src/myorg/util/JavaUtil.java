/**
 * 
 */
package myorg.util;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt) - 9 de Fev de 2012 Misc
 *         utility class
 * 
 */
public class JavaUtil {
    /**
     * 
     * @param object1
     * @param object2
     * @return true if both objects are equal, regardless if they are null or
     *         not.
     */
    public static boolean isObjectEqualTo(Object object1, Object object2) {
	return object1 == null ? object2 == null : object1.equals(object2);
    }

}
