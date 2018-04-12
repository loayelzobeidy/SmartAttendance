import networkx as nx
import matplotlib.pyplot as plt
g = nx.Graph()
G = nx.Graph()
g.add_edge('a', 'b', weight=0.1)
g.add_edge('b', 'c', weight=1.5)
g.add_edge('a', 'c', weight=1.0)
g.add_edge('c', 'd', weight=2.2)
g.add_nodes_from([2, 6])
print nx.shortest_path(g, 'b', 'd')
print nx.shortest_path(g, 'b', 'd', weight='weight')
h = nx.path_graph(5)
g.add_nodes_from(h)
print(g.nodes())
fruit_dict = {'apple': 1, 'orange': [0.12, 0.02], 42: True}
g.add_node(1,time=fruit_dict)
print(g.node[1]['time']['orange'])
g.add_edge(1, 2, weight=4.0)
g[1][2]['weight'] = 5.0
g[1][2]
g.add_edge(1, 2)
for node in g.nodes():
    print node, g.degree(node)
G.add_nodes_from([1, 10])
print(G.nodes())
H = nx.path_graph(10)
G.add_nodes_from(H)
print(H.nodes())
print(G.nodes())
G.add_edge(1, 2)
H = nx.DiGraph(G)
print(list(H.edges()))
print(list(G.edges()))
G = nx.petersen_graph()
plt.subplot(121)
nx.draw(G, with_labels=True, font_weight='bold')
plt.subplot(122)
nx.draw_shell(G, nlist=[range(5, 10), range(5)], with_labels=True, font_weight='bold')
plt.show()