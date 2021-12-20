package geo;

/** Describes a tree. Is mostly used by the GUI */
public interface Tree {

    String getName();

    Tree getLeft();

    Tree getRight();

    int getDepth();

    int hashCode();

    boolean equals(Object other);

}
