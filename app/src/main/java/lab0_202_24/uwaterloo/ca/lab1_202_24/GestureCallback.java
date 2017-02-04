package lab0_202_24.uwaterloo.ca.lab1_202_24;

/**
 * Created by patri on 2017-02-04.
 */

public interface GestureCallback {
    public static enum Direction{LEFT, RIGHT, UP, DOWN};
    public void onGestureDetect(Direction direction);
}
