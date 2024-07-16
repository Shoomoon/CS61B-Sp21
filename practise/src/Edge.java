import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Edge<T> {
     public T u;
     public T v;
     public Edge(T i, T j) {
         u = i;
         v = j;
     }
     public void addEdge(Map<T, Set<T>> map) {
         if (!map.containsKey(u)) {
             map.put(u, new HashSet<>());
         }
         if (!map.containsKey(v)) {
             map.put(v, new HashSet<>());
         }
         map.get(u).add(v);
         map.get(v).add(u);
     }
     public void removeEdge(Map<T, Set<T>> map) {
         map.get(u).remove(v);
         map.get(v).remove(u);
     }
}
