import java.util.*;

public class Graph {
    private Map<Integer, Set<Integer>> map;
    public Graph(List<Edge> edges) {
        this.map = new HashMap<>();
        for (Edge edge: edges) {
            edge.addEdge(map);
        }
    }
    public boolean hasEulerTour() {
        int count = 0;
        for (Set<Integer> neighbours: map.values()) {
            if (neighbours.size() % 2 == 1) {
                count += 1;
                if (count > 2) {
                    return false;
                }
            }
        }
        return true;
    }
    public List<Integer> oddIndeedVertexes() {
        List<Integer> res = new ArrayList<>();
        for (int u: map.keySet()) {
            if (map.get(u).size() % 2 == 1) {
                res.add(u);
            }
        }
        return res;
    }
    public void removeEdge(int u, int v) {
        map.get(u).remove(v);
        map.get(v).remove(u);
    }
    public void printEulerTour() {
        List<Integer> startCandidates = oddIndeedVertexes();
        // check if the graph has euler tour, but not sure if the graph is connected
        if (startCandidates.size() != 0 && startCandidates.size() != 2) {
            System.out.println("No Euler Tour exists!");
        }
        int curNode = map.keySet().iterator().next();
        if (startCandidates.size() == 2) {
            curNode = startCandidates.get(0);
        }
        List<Integer> eulerTour = new ArrayList<>();
        eulerTour.add(curNode);
        while (map.get(curNode).size() != 0) {
            boolean bridgeFound = false;
            int nextNode = 0;
            for (int v: map.get(curNode)) {
                if (map.get(v).size() % 2 == 0) {
                    nextNode = v;
                    bridgeFound = true;
                    break;
                }
            }
            if (!bridgeFound) {
                nextNode = map.get(curNode).iterator().next();
            }
            removeEdge(curNode, nextNode);
            curNode = nextNode;
            eulerTour.add(curNode);
        }
        // if the result doesn't contain all nodes, then the graph is not connected, hence no Euler Tour exists.
        if (eulerTour.size() != map.size()) {
            System.out.println("No Euler Tour exists!");
        }
        System.out.println(eulerTour.toString());
    }
}
