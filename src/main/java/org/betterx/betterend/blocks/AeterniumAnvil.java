package org.betterx.betterend.blocks;

import org.betterx.bclib.items.BaseAnvilItem;
import org.betterx.betterend.blocks.basis.EndAnvilBlock;
import org.betterx.betterend.item.material.EndToolMaterial;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public class AeterniumAnvil extends EndAnvilBlock {
    public AeterniumAnvil() {
        super(EndBlocks.AETERNIUM_BLOCK.defaultMapColor(), EndToolMaterial.AETERNIUM.getLevel());
    }

    @Override
    public int getMaxDurability() {
        return 12;
    }

    @Override
    public BlockItem getCustomBlockItem(Identifier blockID, Item.Properties settings) {
        return new BaseAnvilItem(this, settings.fireResistant());
    }
}


class Graph<T> implements java.lang.Iterable<Node<T>> {
    private final java.util.LinkedList<Node<T>> nodes;

    public Graph() {
        this.nodes = new java.util.LinkedList<>();
    }

    public Node<T> add(T payload) {
        Node<T> node = new Node<>(payload);
        this.nodes.add(node);
        return node;
    }

    public Edge<T> addEdge(T n1, T n2) {
        return this.addEdge(n1, n2, 1.0);
    }

    public Edge<T> addEdge(T n1, T n2, double weight) {
        Node<T> source = this.getForPayload(n1);
        Node<T> target = this.getForPayload(n2);
        if (source != null && target != null) {
            return source.addEdge(target, weight);
        }
        return null;
    }

    public Edge<T> addEdge(Node<T> source, Node<T> target) {
        return this.addEdge(source, target, 1.0);
    }

    public Edge<T> addEdge(Node<T> source, Node<T> target, double weight) {
        if (source != null && target != null) {
            return source.addEdge(target, weight);
        }
        return null;
    }

    public Node<T> getForPayload(T payload) {
        return this.nodes.stream().filter(n -> n.getPayload().equals(payload)).findFirst().orElse(null);
    }

    @NotNull
    @Override
    public Iterator<Node<T>> iterator() {
        return nodes.iterator();
    }
}

class Node<T> {
    public final T payload;
    public final java.util.List<Edge<T>> children;

    public T getPayload() {
        return this.payload;
    }

    public java.util.List<Node<T>> childNodes() {
        return this.children.stream()
                            .map(e -> e.target)
                            .collect(
                                    () -> new java.util.LinkedList<Node<T>>(),
                                    (list, node) -> list.add(node),
                                    (list1, list2) -> list1.addAll(list2)
                            );
    }

    public Edge<T> addEdge(Node<T> target, double weight) {
        Edge<T> e = new Edge<>(this, target, weight);
        this.children.add(e);
        return e;
    }

    public Node(T payload) {
        this.payload = payload;
        this.children = new java.util.LinkedList<>();
    }
}

class Edge<T> {
    public final Node<T> source;
    public final Node<T> target;
    public final double weight;

    public Edge(Node<T> source, Node<T> target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }
}
