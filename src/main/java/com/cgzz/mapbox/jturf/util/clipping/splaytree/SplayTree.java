package com.cgzz.mapbox.jturf.util.clipping.splaytree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * https://github.com/w8r/splay-tree
 *
 * splaytree v3.1.0
 * Fast Splay tree for Node and browser
 *
 * @author Alexander Milevski <info@w8r.name>
 * @license MIT
 * @preserve
 */
// 首先，伸展树(splay tree)是一颗二叉搜索树，它的定义是建立在二叉搜索树之上，
// 并且它是基于类似程序局部性原理的假设：一个节点在一次被访问后，这个节点很可能不久再次被访问。
// 那么伸展树的做法就是在每次一个节点被访问后，我们就把它推到树根的位置。
// 正像程序局部性原理的实际效率被广泛证明一样，伸展树在实际的搜索效率上也是非常高效的。
// 尽管存在最坏情况下单次操作会花费O(N)的时间，但是这种情况并不是经常发生，而实际证明伸展树能够保证M次连续操作最多花费O(MlogN)的时间。
//
// 相比于平衡二叉树，伸展树有差不多的平均性能，其他的优势在于：不需要存储平衡信息。另外如果采用自顶向下的调整方式，还能简略额外的栈开销。
public class SplayTree<K extends Comparable<K>, V> {

    public static class TreeNodeList<K extends Comparable<K>, V> {
        Node<K, V> head;

        public TreeNodeList(Node<K, V> head) {
            this.head = head;
        }
    }

    private final Comparator<? super K> _comparator;
    private Node<K, V> _root;
    private int _size = 0;

    public SplayTree() {
        _comparator = Comparable::compareTo;
    }

    public SplayTree(Comparator<? super K> comparator) {
        this._comparator = comparator;
    }

    // Simple top down splay, not requiring i to be in the tree t.
    public Node<K, V> splay(K i, Node<K, V> t) {
        Node<K, V> N = new Node<>(null, null);
        Node<K, V> l = N, r = N;

        while (true) {
            int cmp = this._comparator.compare(i, t.key);
            if (cmp < 0) {
                if (t.left == null) {
                    break;
                }
                if (this._comparator.compare(i, t.left.key) < 0) {
                    Node<K, V> y = t.left;                           /* rotate right */
                    t.left = y.right;
                    y.right = t;
                    t = y;
                    if (t.left == null) {
                        break;
                    }
                }
                r.left = t;                               /* link right */
                r = t;
                t = t.left;
            } else if (cmp > 0) {
                if (t.right == null) {
                    break;
                }
                if (this._comparator.compare(i, t.right.key) > 0) {
                    Node<K, V> y = t.right;                          /* rotate left */
                    t.right = y.left;
                    y.left = t;
                    t = y;
                    if (t.right == null) {
                        break;
                    }
                }
                l.right = t;                              /* link left */
                l = t;
                t = t.right;
            } else {
                break;
            }
        }

        /* assemble */
        l.right = t.left;
        r.left = t.right;
        t.left = N.right;
        t.right = N.left;
        return t;
    }

    public Node<K, V> insert(K i, V data, Node<K, V> t) {
        Node<K, V> node = new Node<>(i, data);
        if (t == null) {
            node.left = node.right = null;
            return node;
        }

        t = splay(i, t);
        int cmp = this._comparator.compare(i, t.key);
        if (cmp < 0) {
            node.left = t.left;
            node.right = t;
            t.left = null;
        } else {
            node.right = t.right;
            node.left = t;
            t.right = null;
        }
        return node;
    }

    public Node<K, V> split(K key, Node<K, V> v) {
        Node<K, V> left = null, right = null;
        if (v != null) {
            v = splay(key, v);

            int cmp = this._comparator.compare(v.key, key);
            if (cmp == 0) {
                left = v.left;
                right = v.right;
            } else if (cmp < 0) {
                right = v.right;
                v.right = null;
                left = v;
            } else {
                left = v.left;
                v.left = null;
                right = v;
            }
        }

        Node<K, V> n = new Node<>(null, null);
        n.left = left;
        n.right = right;
        return n;
    }

    public Node<K, V> merge(Node<K, V> left, Node<K, V> right) {
        if (right == null) {
            return left;
        }
        if (left == null) {
            return right;
        }

        right = splay(left.key, right);
        right.left = left;

        return right;
    }

    /**
     * Inserts a key, allows duplicates
     */
    public Node<K, V> insert(K key) {
        return this.insert(key, null);
    }

    /**
     * Inserts a key, allows duplicates
     */
    public Node<K, V> insert(K key, V data) {
        this._size++;
        return this._root = insert(key, data, this._root);
    }

    public Node<K, V> add(K key) {
        return this.add(key, null);
    }

    /**
     * Adds a key, if it is not present in the tree
     */
    public Node<K, V> add(K key, V data) {
        Node<K, V> node = new Node<>(key, data);
        if (this._root == null) {
            node.left = node.right = null;
            this._size++;
            this._root = node;
        }

        Node<K, V> t = splay(key, this._root);
        int cmp = this._comparator.compare(key, t.key);
        if (cmp == 0) {
            this._root = t;
        } else {
            if (cmp < 0) {
                node.left = t.left;
                node.right = t;
                t.left = null;
            } else {
                node.right = t.right;
                node.left = t;
                t.right = null;
            }
            this._size++;
            this._root = node;
        }

        return this._root;
    }

    public void remove(K key) {
        this._root = this._remove(key, this._root);
    }

    // Deletes i from the tree if it's there
    private Node<K, V> _remove(K i, Node<K, V> t) {
        Node<K, V> x;
        if (t == null) {
            return null;
        }
        t = splay(i, t);
        int cmp = this._comparator.compare(i, t.key);
        if (cmp == 0) {               /* found it */
            if (t.left == null) {
                x = t.right;
            } else {
                x = splay(i, t.left);
                x.right = t.right;
            }
            this._size--;
            return x;
        }
        return t;                         /* It wasn't there */
    }

    // Removes and returns the node with smallest key
    public Node<K, V> pop() {
        Node<K, V> node = this._root;
        if (node != null) {
            while (node.left != null) {
                node = node.left;
            }
            this._root = splay(node.key, this._root);
            this._root = this._remove(node.key, this._root);

            return new Node<>(node.key, node.data);
        }
        return null;
    }

    // Find without splaying
    public Node<K, V> findStatic(K key) {
        Node<K, V> current = this._root;
        while (current != null) {
            int cmp = this._comparator.compare(key, current.key);
            if (cmp == 0) {
                return current;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    public Node<K, V> find(K key) {
        if (this._root != null) {
            this._root = splay(key, this._root);
            if (this._comparator.compare(key, this._root.key) != 0) {
                return null;
            }
        }
        return this._root;
    }

    public boolean contains(K key) {
        Node<K, V> current = this._root;
        while (current != null) {
            int cmp = this._comparator.compare(key, current.key);
            if (cmp == 0) {
                return true;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return false;
    }

    public SplayTree<K, V> forEach(Consumer<Node<K, V>> visitor) {
        Node<K, V> current = this._root;
        LinkedList<Node<K, V>> Q = new LinkedList<>();  /* Initialize stack s */
        boolean done = false;

        while (!done) {
            if (current != null) {
                Q.push(current);
                current = current.left;
            } else {
                if (Q.size() != 0) {
                    current = Q.pop();
                    visitor.accept(current);

                    current = current.right;
                } else done = true;
            }
        }
        return this;
    }

    // Walk key range from `low` to `high`. Stops if `fn` returns a value.
    public SplayTree<K, V> range(K low, K high, Function<Node<K, V>, Boolean> visitor) {
        LinkedList<Node<K, V>> Q = new LinkedList<>();
        Node<K, V> node = this._root;
        int cmp;

        while (Q.size() != 0 || node != null) {
            if (node != null) {
                Q.push(node);
                node = node.left;
            } else {
                node = Q.pop();
                cmp = this._comparator.compare(node.key, high);
                if (cmp > 0) {
                    break;
                } else if (this._comparator.compare(node.key, low) >= 0) {
                    if (visitor.apply(node)) {
                        return this; // stop if smth is returned
                    }
                }
                node = node.right;
            }
        }
        return this;
    }

    // Returns array of keys
    public List<K> keys() {
        List<K> keys = new ArrayList<>();
        this.forEach(node -> keys.add(node.key));
        return keys;
    }

    // Returns array of all the data in the nodes
    public List<V> value() {
        List<V> values = new ArrayList<>();
        this.forEach(node -> values.add(node.data));
        return values;
    }

    public K min() {
        if (this._root != null) {
            return this.minNode(this._root).key;
        }
        return null;
    }

    public K max() {
        if (this._root != null) {
            return this.maxNode(this._root).key;
        }
        return null;
    }

    public Node<K, V> minNode(Node<K, V> t) {
        if (t == null) {
            t = this._root;
        }
        if (t != null) {
            while (t.left != null) {
                t = t.left;
            }
        }
        return t;
    }

    public Node<K, V> maxNode(Node<K, V> t) {
        if (t == null) {
            t = this._root;
        }
        if (t != null) {
            while (t.right != null) {
                t = t.right;
            }
        }
        return t;
    }

    // Returns node at given index
    public Node<K, V> at(int index) {
        Node<K, V> current = this._root;
        boolean done = false;
        int i = 0;
        LinkedList<Node<K, V>> Q = new LinkedList<>();

        while (!done) {
            if (current != null) {
                Q.push(current);
                current = current.left;
            } else {
                if (Q.size() > 0) {
                    current = Q.pop();
                    if (i == index) {
                        return current;
                    }
                    i++;
                    current = current.right;
                } else {
                    done = true;
                }
            }
        }
        return null;
    }

    public Node<K, V> next(Node<K, V> d) {
        Node<K, V> root = this._root;
        Node<K, V> successor = null;

        if (d.right != null) {
            successor = d.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            return successor;
        }

        while (root != null) {
            int cmp = this._comparator.compare(d.key, root.key);
            if (cmp == 0) {
                break;
            } else if (cmp < 0) {
                successor = root;
                root = root.left;
            } else {
                root = root.right;
            }
        }

        return successor;
    }

    public Node<K, V> prev(Node<K, V> d) {
        Node<K, V> root = this._root;
        Node<K, V> predecessor = null;

        if (d.left != null) {
            predecessor = d.left;
            while (predecessor.right != null) {
                predecessor = predecessor.right;
            }
            return predecessor;
        }

        while (root != null) {
            int cmp = this._comparator.compare(d.key, root.key);
            if (cmp == 0) {
                break;
            } else if (cmp < 0) {
                root = root.left;
            } else {
                predecessor = root;
                root = root.right;
            }
        }
        return predecessor;
    }

    public SplayTree<K, V> clear() {
        this._root = null;
        this._size = 0;
        return this;
    }

    public Node<K, V> toList() {
        return toList(this._root);
    }

    // Bulk-load items. Both array have to be same size
    public SplayTree<K, V> load(List<K> keys, List<V> values, boolean presort) {
        int size = keys.size();

        // sort if needed
        if (presort) {
            sort(keys, values, 0, size - 1);
        }

        if (this._root == null) { // empty tree
            this._root = loadRecursive(keys, values, 0, size);
            this._size = size;
        } else { // that re-builds the whole tree from two in-order traversals
            Node<K, V> mergedList = mergeLists(this.toList(), createList(keys, values));
            size = this._size + size;
            this._root = sortedListToBST(new TreeNodeList<>(mergedList), 0, size);
        }
        return this;
    }

    public boolean isEmpty() {
        return this._root == null;
    }

    public int size() {
        return this._size;
    }

    public Node<K, V> root() {
        return this._root;
    }

    public void printRow(Node<K, V> root, String prefix, boolean isTail, StringBuilder buf) {
        if (root == null) {
            return;
        }

        buf.append(prefix).append(isTail ? "└── " : "├── ").append(root.key).append("\n");
        String indent = prefix + (isTail ? "    " : "│   ");
        if (root.left != null) {
            printRow(root.left, indent, false, buf);
        }
        if (root.right != null) {
            printRow(root.right, indent, true, buf);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        printRow(this._root, "", true, buf);
        return buf.toString();
    }

    public void update(K key, K newKey, V newData) {
        Node<K, V> temp = split(key, this._root);
        Node<K, V> left = temp.left, right = temp.right;
        if (this._comparator.compare(key, newKey) < 0) {
            right = insert(newKey, newData, right);
        } else {
            left = insert(newKey, newData, left);
        }
        this._root = merge(left, right);
    }

    public Node<K, V> split(K key) {
        return split(key, this._root);
    }

    private Node<K, V> loadRecursive(List<K> keys, List<V> values, int start, int end) {
        int size = end - start;
        if (size > 0) {
            int middle = start + (int) Math.floor(size / 2);
            K key = keys.get(middle);
            V data = values.get(middle);
            Node<K, V> node = new Node<>(key, data);
            node.left = loadRecursive(keys, values, start, middle);
            node.right = loadRecursive(keys, values, middle + 1, end);
            return node;
        }
        return null;
    }

    private Node<K, V> createList(List<K> keys, List<V> values) {
        Node<K, V> head = new Node<>(null, null);
        Node<K, V> p = head;
        for (int i = 0, size = keys.size(); i < size; i++) {
            p = p.next = new Node<>(keys.get(i), values.get(i));
        }
        p.next = null;
        return head.next;
    }

    private Node<K, V> toList(Node<K, V> root) {
        Node<K, V> current = root;
        LinkedList<Node<K, V>> Q = new LinkedList<>();
        boolean done = false;

        Node<K, V> head = new Node<>(null, null);
        Node<K, V> p = head;

        while (!done) {
            if (current != null) {
                Q.push(current);
                current = current.left;
            } else {
                if (Q.size() > 0) {
                    current = p = p.next = Q.pop();
                    current = current.right;
                } else {
                    done = true;
                }
            }
        }
        p.next = null; // that'll work even if the tree was empty
        return head.next;
    }

    private Node<K, V> sortedListToBST(TreeNodeList<K, V> list, int start, int end) {
        int size = end - start;
        if (size > 0) {
            int middle = start + (int) Math.floor(size / 2);
            Node<K, V> left = sortedListToBST(list, start, middle);

            Node<K, V> root = list.head;
            root.left = left;

            list.head = list.head.next;

            root.right = sortedListToBST(list, middle + 1, end);
            return root;
        }
        return null;
    }

    private Node<K, V> mergeLists(Node<K, V> l1, Node<K, V> l2) {
        Node<K, V> head = new Node<>(null, null); // dummy
        Node<K, V> p = head;

        Node<K, V> p1 = l1;
        Node<K, V> p2 = l2;

        while (p1 != null && p2 != null) {
            if (this._comparator.compare(p1.key, p2.key) < 0) {
                p.next = p1;
                p1 = p1.next;
            } else {
                p.next = p2;
                p2 = p2.next;
            }
            p = p.next;
        }

        if (p1 != null) {
            p.next = p1;
        } else if (p2 != null) {
            p.next = p2;
        }

        return head.next;
    }

    private void sort(List<K> keys, List<V> values, int left, int right) {
        if (left >= right) {
            return;
        }

        K pivot = keys.get((left + right) >> 1);
        int i = left - 1;
        int j = right + 1;

        while (true) {
            do i++; while (this._comparator.compare(keys.get(i), pivot) < 0);
            do j--; while (this._comparator.compare(keys.get(j), pivot) > 0);
            if (i >= j) break;

            K k = keys.get(i);
            keys.set(i, keys.get(j));
            keys.set(j, k);

            V v = values.get(i);
            values.set(i, values.get(j));
            values.set(j, v);
        }

        sort(keys, values, left, j);
        sort(keys, values, j + 1, right);
    }


}
