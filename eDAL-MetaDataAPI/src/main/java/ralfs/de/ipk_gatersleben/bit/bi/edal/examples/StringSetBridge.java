package ralfs.de.ipk_gatersleben.bit.bi.edal.examples;
/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 *
 * We have chosen to apply the GNU General Public License (GPL) Version 3 (https://www.gnu.org/licenses/gpl-3.0.html)
 * to the copyrightable parts of e!DAL, which are the source code, the executable software, the training and
 * documentation material. This means, you must give appropriate credit, provide a link to the license, and indicate
 * if changes were made. You are free to copy and redistribute e!DAL in any medium or format. You are also free to
 * adapt, remix, transform, and build upon e!DAL for any purpose, even commercially.
 *
 *  Contributors:
 *       Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany
 */
import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;
public class StringSetBridge implements ValueBridge<HashSet<String>, String> {

	@Override
	public String toIndexedValue(HashSet<String> value, ValueBridgeToIndexedValueContext context) {
        if(value != null)
        {
        	StringBuilder  buf = new StringBuilder ();

            HashSet<?> col = (HashSet<?>)value;
            Iterator<?> it = col.iterator();
            while(it.hasNext())
            {
                String next = it.next().toString();
                buf.append(next);
                if(it.hasNext())
                    buf.append(", ");
            }
            return buf.toString();
        }
        return null;
	}
} 