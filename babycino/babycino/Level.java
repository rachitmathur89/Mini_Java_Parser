package babycino;

// A security level.
public enum Level {
    LOW,
    HIGH;

    // Least upper bound of two security levels.
    public static Level lub(Level l1, Level l2) {
		
		return l1.equals(Level.LOW) ? l2 : Level.HIGH;
        // TODO: Task 1.1
      
    }

    // Greatest lower bound of two security levels.
    public static Level glb(Level l1, Level l2) {
		return l1.equals(Level.HIGH) ? l2 : Level.LOW;
        // TODO: Task 1.2
	
    }

    // Less than or equal on security levels.
    public static boolean le(Level l1, Level l2) {
        return ((l1 == l2) || (l1 == Level.LOW));
    }

}

