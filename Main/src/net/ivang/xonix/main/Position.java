package net.ivang.xonix.main;

/**
 * @author Ivan Gadzhega
 * @version $Id$
 * @since 0.1
 */
public class Position {
    public int x;
    public int y;

    public Position(int x, int y){
        this.x = x;
        this.y =y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Position))
            return false;
        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 3;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }
}
