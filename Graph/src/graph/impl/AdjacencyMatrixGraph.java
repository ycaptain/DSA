package graph.impl;

import graph.core.BFSLabel;
import graph.core.DFSLabel;
import graph.core.IEdge;
import graph.core.IGraph;
import graph.core.IIterator;
import graph.core.IList;
import graph.core.INode;
import graph.core.IVertex;
import graph.util.DLinkedList;

public class AdjacencyMatrixGraph<V,E> implements IGraph<V,E> {
	/**
	 * Inner class to represent a vertex in an adjacency matrix graph implementation
	 */
	private class AdjacencyMatrixVertex implements IVertex<V> {
		// label for depth first search
		DFSLabel dfsLabel;
		
		// label for breath first search
		BFSLabel bfsLabel;
		
		// reference to a node in the vertex list
		INode<IVertex<V>> node;
		
		// key associated with vertex
		int index;

		// element stored in this vertex
		V element;
		
		public AdjacencyMatrixVertex(V element) {
			this.element = element;
			// give its key
			this.index = maxIndex;
		}
		
		@Override
		public V element() {
			return element;
		}
		
		// It's useful to have a toString() method that can
		// return details about this object, so you can
		// print the object later and get useful information.
		// This one prints the element
		public String toString() {
			return element.toString();
		}
	}

	/**
	 * Inner class to represent an edge in an edge list graph implementation.
	 *
	 */
	private class AdjacencyMatrixEdge implements IEdge<E> {
		// label for depth first search
		DFSLabel dfsLabel;
		
		// label for breath first search
		BFSLabel bfsLabel;
		
		// reference to a node in the edge list
		INode<IEdge<E>> node;
		
		// element stored in this edge
		E element;
		
		// the start and end vertices that this edge connects
		AdjacencyMatrixVertex start, end;
		
		// constructor to set the three fields
		public AdjacencyMatrixEdge(AdjacencyMatrixVertex start, AdjacencyMatrixVertex end, E element) {
			this.start = start;
			this.end = end;
			this.element = element;
		}

		@Override
		public E element() {
			return element;
		}
		
		public String toString() {
			return element.toString();
		}
		
	}
	
	// vertex list
	private IList<IVertex<V>> vertices;
	
	// edge list
	private IList<IEdge<E>> edges;
	
	// adjacency array
	private int maxIndex = 0;
	private IEdge<E>[][] adjacencyMatrix;
	
	/**
	 * Constructor
	 */
	@SuppressWarnings("unchecked")
	public AdjacencyMatrixGraph() {
		// create new (empty) lists of edges and vertices
		vertices = new DLinkedList<IVertex<V>>();
		edges = new DLinkedList<IEdge<E>>();
		adjacencyMatrix = new IEdge[maxIndex][maxIndex];
	}
	
	@Override
	public IVertex<V>[] endVertices(IEdge<E> e) {
		// need to cast Edge type to AdjacencyMatrixEdge
		AdjacencyMatrixEdge edge = (AdjacencyMatrixEdge) e;
		
		// create new array of length 2 that will contain
		// the edge's end vertices
		@SuppressWarnings("unchecked")
		IVertex<V>[] endpoints = new IVertex[2];
		
		// fill array
		endpoints[0] = edge.start;
		endpoints[1] = edge.end;
		
		return endpoints;
	}

	@Override
	public IVertex<V> opposite(IVertex<V> v, IEdge<E> e) {
		// find end points of Edge e
		IVertex<V>[] endpoints = endVertices(e);
		
		// return the end point that is not v
		if (endpoints[0].equals(v)) {
			return endpoints[1];
		} else if (endpoints[1].equals(v)) {
			return endpoints[0];
		}
		
		// Problem! e is not connected to v.
		throw new RuntimeException("Error: cannot find opposite vertex.");
	}

	@Override
	public boolean areAdjacent(IVertex<V> v, IVertex<V> w) {
		// need to cast vertex type to AdjacencyMatrixVertex
		AdjacencyMatrixVertex vertexV = (AdjacencyMatrixVertex) v;
		AdjacencyMatrixVertex vertexW = (AdjacencyMatrixVertex) w;
		// get keys
		int keyV = vertexV.index;
		int keyW = vertexW.index;
		
		// matrix connects same edge (so they are adjacent)
		if(adjacencyMatrix[keyV][keyW].equals(adjacencyMatrix[keyW][keyV]))
			return true;
		// matrix connects different edges (so they are not adjacent)
		return false;
	}

	@Override
	public V replace(IVertex<V> v, V o) {
		AdjacencyMatrixVertex vertex = (AdjacencyMatrixVertex) v;
		// store old element that we should return
		V temp = vertex.element;
		
		// do the replacement
		vertex.element = o;
		
		//return the old value
		return temp;
	}

	@Override
	public E replace(IEdge<E> e, E o) {
		AdjacencyMatrixEdge edge = (AdjacencyMatrixEdge) e;
		E temp = edge.element;
		edge.element = o;
		return temp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IVertex<V> insertVertex(V o) {
		// create new vertex
		AdjacencyMatrixVertex vertex = new AdjacencyMatrixVertex(o);
		
		// insert the vertex into the vertex list
		// (returns a reference to the new Node that was created)
		INode<IVertex<V>> node = vertices.insertLast(vertex);
		
		// this reference must be stored in the vertex
		// to make it easier to remove the vertex later
		vertex.node = node;
		
		// recreate matrix
		maxIndex++;
		IEdge<E>[][] e = new IEdge[maxIndex][maxIndex];
		IIterator<IEdge<E>> it = edges.iterator();
		IVertex<V>[] endpoints;
		IEdge<E> edge;
		int x, y;
		while(it.hasNext()) {
			edge = it.next();
			endpoints = endVertices(edge);
			y = ((AdjacencyMatrixVertex)endpoints[0]).index;
			x = ((AdjacencyMatrixVertex)endpoints[1]).index;
			e[y][x] = edge;
		}
		adjacencyMatrix = e;
		
		// return the new vertex that was created
		return vertex;
	}

	@Override
	public IEdge<E> insertEdge(IVertex<V> v, IVertex<V> w, E o) {
		// need to cast vertex type to AdjacencyMatrixVertex
		AdjacencyMatrixVertex vertexV = (AdjacencyMatrixVertex) v;
		AdjacencyMatrixVertex vertexW = (AdjacencyMatrixVertex) w;
		// create new edge object
		AdjacencyMatrixEdge edge = new AdjacencyMatrixEdge(vertexV , vertexW, o);
		
		// insert into the edge list and store the reference to te node
		// in the edge object
		INode<IEdge<E>> n = edges.insertLast(edge);
		edge.node = n;
		
		// add edge to matrix
		adjacencyMatrix[vertexV.index][vertexW.index] = edge;
		adjacencyMatrix[vertexW.index][vertexV.index] = edge;
		
		// return the new edge that was created
		return edge;
	}

	@Override
	public V removeVertex(IVertex<V> v) {
		// first find all incident edges and remove those
		IList<IEdge<E>> incidentEdges = new DLinkedList<IEdge<E>>();
		IIterator<IEdge<E>> it = incidentEdges(v);
		while( it.hasNext() )
			incidentEdges.insertLast(it.next());
		
		while (!incidentEdges.isEmpty())
			removeEdge(incidentEdges.remove(incidentEdges.first()));

		// now we can remove the vertex from the vertex list
		AdjacencyMatrixVertex vertex = (AdjacencyMatrixVertex) v;
		vertices.remove(vertex.node);
		
		@SuppressWarnings("unchecked")
		IEdge<E>[][] e = new IEdge[maxIndex][maxIndex];
		System.out.println(maxIndex);
		IIterator<IEdge<E>> itE = edges.iterator();
		IVertex<V>[] endpoints;
		IEdge<E> edge;
		int x, y;
		while(itE.hasNext()) {
			edge = itE.next();
			endpoints = endVertices(edge);
			y = ((AdjacencyMatrixVertex)endpoints[0]).index;
			x = ((AdjacencyMatrixVertex)endpoints[1]).index;
			e[y][x] = edge;
		}
		adjacencyMatrix = e;

		// return the element of the vertex that was removed
		return vertex.element;
	}

	@Override
	public E removeEdge(IEdge<E> e) {
		// remove edge from edge list and return its element
		AdjacencyMatrixEdge edge = (AdjacencyMatrixEdge) e;
		edges.remove(edge.node);
		return edge.element;
	}

	@Override
	public IIterator<IEdge<E>> incidentEdges(IVertex<V> v) {
		// strategy:
		// find all edges that are connected to v and
		// add them to "list".
		// Later, use the iterator() method in List to
		// get an iterator over these edges.
		IList<IEdge<E>> list = new DLinkedList<IEdge<E>>();

		IIterator<IEdge<E>> it = edges.iterator();
		while (it.hasNext()) {
			AdjacencyMatrixEdge edge = (AdjacencyMatrixEdge) it.next();
			if (edge.start.equals(v))
				list.insertLast(edge);
			if (edge.end.equals(v))
				list.insertLast(edge);
		}
		return list.iterator();
	}

	@Override
	public IIterator<IVertex<V>> vertices() {
		return vertices.iterator();
	}

	@Override
	public IIterator<IEdge<E>> edges() {
		return edges.iterator();
	}
	
	/**
	 * labeling of the edges of g as discovery edges and back edges
	 */
	public void DFS(IGraph<V, E> g) {
		for(IIterator<IVertex<V>> uIterator = g.vertices(); uIterator.hasNext();) {
			IVertex<V> u = uIterator.next();
			setLabel(u, DFSLabel.UNEXPLORED);
		}
		for(IIterator<IEdge<E>> eIterator = g.edges(); eIterator.hasNext();) {
			IEdge<E> e = eIterator.next();
			setLabel(e, DFSLabel.UNEXPLORED);
		}
		for(IIterator<IVertex<V>> vIterator = g.vertices(); vIterator.hasNext();) {
			IVertex<V> v = vIterator.next();
			if(getLabel(v) == DFSLabel.UNEXPLORED)
				DFS(g, v);
		}
	}
	
	/**
	 * labeling of the edges of g in the connected component of v as
	 * discovery edges and back edges
	 */
	public void DFS(IGraph<V, E> g, IVertex<V> v) {
		setLabel(v, DFSLabel.VISITED);
		for(IIterator<IEdge<E>> eIterator = g.incidentEdges(v); eIterator.hasNext();) {
			IEdge<E> e = eIterator.next();
			if(getLabel(e) == DFSLabel.UNEXPLORED) {
				IVertex<V> w = g.opposite(v, e);
				if(getLabel(w) == DFSLabel.UNEXPLORED) {
					setLabel(e, DFSLabel.DISCOVERY);
					DFS(g, w);
				} else {
					setLabel(e, DFSLabel.BACK);
				}
			}
		}
		
	}
		
	/**
	 * labeling vertex
	 */
	@SuppressWarnings("unchecked")
	public void setLabel(IVertex<V> u, DFSLabel l) {
		// must cast to EdgeList vertex
		AdjacencyMatrixVertex v = (AdjacencyMatrixVertex) u;
		v.dfsLabel = l;
	}
		
	/**
	 * labeling edge
	 */
	public void setLabel(IEdge<E> e, DFSLabel l) {
		// must cast to EdgeList edge
		AdjacencyMatrixEdge ed = (AdjacencyMatrixEdge) e;
		ed.dfsLabel = l;	
	}
		
	/**
	 * get label of vertex
	 */
	public DFSLabel getLabel(IVertex<V> v) {
		// must cast to EdgeList vertex
		AdjacencyMatrixVertex ve = (AdjacencyMatrixVertex) v;
		return ve.dfsLabel;
	}

	/**
	 * get label of edge
	 */
	public DFSLabel getLabel(IEdge<E> e) {
		// must cast to EdgeList edge
		AdjacencyMatrixEdge ed = (AdjacencyMatrixEdge) e;
		return ed.dfsLabel;
	}
	
	/**
	 * labeling of the edges of g as discovery edges and back edges
	 */
	public void BFS(IGraph<V, E> g) {
		for(IIterator<IVertex<V>> uIterator = g.vertices(); uIterator.hasNext();) {
			IVertex<V> u = uIterator.next();
			setBLabel(u, BFSLabel.UNEXPLORED);
		}
		for(IIterator<IEdge<E>> eIterator = g.edges(); eIterator.hasNext();) {
			IEdge<E> e = eIterator.next();
			setBLabel(e, BFSLabel.UNEXPLORED);
		}
		for(IIterator<IVertex<V>> vIterator = g.vertices(); vIterator.hasNext();) {
			IVertex<V> v = vIterator.next();
			if(getBLabel(v) == BFSLabel.UNEXPLORED)
				DFS(g, v);
		}
	}
	
	/**
	 * labeling of the edges of g in the connected component of v as
	 * discovery edges and back edges
	 */
	public void BFS(IGraph<V, E> g, IVertex<V> s) {
		IList<IVertex<V>>[] l = new DLinkedList[1];
		l[0].insertLast(s);
		setBLabel(s, BFSLabel.VISITED);
		int i = 0;
		while(!l[i].isEmpty()) {
			IList<IVertex<V>>[] newL = new DLinkedList[i+2];
			for(int j = 0; j <= i; j++) {
				newL[j] = l[j];
			}
			l = newL;
			
			for(IIterator<IVertex<V>> vIterator = l[i].iterator(); vIterator.hasNext();) {
				IVertex<V> v = vIterator.next();
				for(IIterator<IEdge<E>> eIterator = g.incidentEdges(v); eIterator.hasNext();) {
					IEdge<E> e = eIterator.next();
					if(getBLabel(e) == BFSLabel.UNEXPLORED) {
						IVertex<V> w = opposite(v, e);
						if(getBLabel(w) == BFSLabel.UNEXPLORED) {
							setBLabel(e, BFSLabel.DISCOVERY);
							setBLabel(w, BFSLabel.VISITED);
							l[i+1].insertLast(w);
						} else {
							setBLabel(e, BFSLabel.CROSS);
						}
					}
				}
			}
			i++;
		}
		
		
	}
	
	/**
	 * labeling vertex
	 */
	@SuppressWarnings("unchecked")
	public void setBLabel(IVertex<V> u, BFSLabel l) {
		// must cast to EdgeList vertex
		AdjacencyMatrixVertex v = (AdjacencyMatrixVertex) u;
		v.bfsLabel = l;
	}
		
	/**
	 * labeling edge
	 */
	public void setBLabel(IEdge<E> e, BFSLabel l) {
		// must cast to EdgeList edge
		AdjacencyMatrixEdge ed = (AdjacencyMatrixEdge) e;
		ed.bfsLabel = l;	
	}
	
	/**
	 * get label of vertex
	 */
	public BFSLabel getBLabel(IVertex<V> v) {
		// must cast to EdgeList vertex
		AdjacencyMatrixVertex ve = (AdjacencyMatrixVertex) v;
		return ve.bfsLabel;
	}

	/**
	 * get label of edge
	 */
	public BFSLabel getBLabel(IEdge<E> e) {
		// must cast to EdgeList edge
		AdjacencyMatrixEdge ed = (AdjacencyMatrixEdge) e;
		return ed.bfsLabel;
	}
}
