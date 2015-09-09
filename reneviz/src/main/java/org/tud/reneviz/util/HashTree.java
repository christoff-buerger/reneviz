package org.tud.reneviz.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HashTree<K,V> {
        private V value;
        private Map<K, HashTree<K,V>> children;
        

        
        public HashTree() {
            this.children = new HashMap<K, HashTree<K,V>>();
		}

		public HashTree<K,V> getEntry(K key) {
        	if (children.containsKey(key)) {
        		return children.get(key);
        	} else {
        		HashTree<K,V> child = new HashTree<K,V>();
        		children.put(key, child);
        		return child;
        	}
        }
        
        public List<K> getElementList() {
        	return new ArrayList<K>(children.keySet());
        }

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}
		
		public String toString() {
			if(value != null) {
				return value.toString();
			} else {
		        String result = " [";
		        
		        for (Entry<K, HashTree<K,V>> entry : children.entrySet()) {
		        	result += "(" + entry.getKey().toString() + ": " + entry.getValue().toString() + ")";
		        }
		        
		        return result + "] ";
			}
		}
}