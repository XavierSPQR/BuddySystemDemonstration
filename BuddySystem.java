import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuddySystem {
    private int totalSize;  // Total memory size (must be power of 2)
    private Node root;      // Root of the memory tree
    private Map<Integer, MemoryBlock> allocatedBlocks;  // Track allocated memory blocks

    // Represents a node in the memory tree
    private static class Node {
        int size;           // Size of this memory block
        int offset;         // Starting position in memory
        boolean allocated;  // Whether this block is allocated
        Node left;         // Left child (buddy)
        Node right;        // Right child (buddy)

        Node(int size, int offset) {
            this.size = size;
            this.offset = offset;
            this.allocated = false;
        }
    }

    // Represents an allocated memory block
    public static class MemoryBlock {
        private int id;            // Unique identifier
        private int size;          // Size of allocation
        private int offset;        // Starting position in memory

        MemoryBlock(int id, int size, int offset) {
            this.id = id;
            this.size = size;
            this.offset = offset;
        }

        // Add getters
        public int getId() {
            return id;
        }

        public int getSize() {
            return size;
        }

        public int getOffset() {
            return offset;
        }
    }

    public BuddySystem(int totalSize) {
        // Ensure total size is a power of 2
        if (!isPowerOfTwo(totalSize)) {
            throw new IllegalArgumentException("Total size must be a power of 2");
        }
        this.totalSize = totalSize;
        this.root = new Node(totalSize, 0);
        this.allocatedBlocks = new HashMap<>();
    }

    // Check if a number is a power of 2
    private boolean isPowerOfTwo(int n) {
        return (n & (n - 1)) == 0;
    }

    // Find the smallest power of 2 that can accommodate the requested size
    private int getNextPowerOfTwo(int size) {
        int power = 1;
        while (power < size) {
            power *= 2;
        }
        return power;
    }

    // Allocate memory of given size
    public MemoryBlock allocate(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }

        int actualSize = getNextPowerOfTwo(size);
        Node node = allocateMemory(root, actualSize);
        
        if (node != null) {
            int id = allocatedBlocks.size() + 1;
            MemoryBlock block = new MemoryBlock(id, actualSize, node.offset);
            allocatedBlocks.put(id, block);
            return block;
        }
        
        return null;  // Allocation failed
    }

    // Recursive helper method for allocation
    private Node allocateMemory(Node node, int size) {
        if (node == null || node.allocated || node.size < size) {
            return null;
        }

        if (node.size == size) {
            if (!node.allocated) {
                node.allocated = true;
                return node;
            }
            return null;
        }

        // Split the block if it's too large
        if (node.left == null && node.right == null) {
            node.left = new Node(node.size / 2, node.offset);
            node.right = new Node(node.size / 2, node.offset + node.size / 2);
        }

        Node result = allocateMemory(node.left, size);
        if (result == null) {
            result = allocateMemory(node.right, size);
        }
        return result;
    }

    // Free allocated memory block
    public boolean free(int blockId) {
        MemoryBlock block = allocatedBlocks.get(blockId);
        if (block == null) {
            return false;
        }

        freeMemory(root, block.getOffset(), block.getSize());
        allocatedBlocks.remove(blockId);
        return true;
    }

    // Recursive helper method for freeing memory
    private void freeMemory(Node node, int offset, int size) {
        if (node == null) return;

        if (node.offset == offset && node.size == size) {
            node.allocated = false;
            mergeBuddies(root);
            return;
        }

        freeMemory(node.left, offset, size);
        freeMemory(node.right, offset, size);
    }

    // Merge buddy blocks if both are free
    private boolean mergeBuddies(Node node) {
        if (node == null || node.left == null || node.right == null) {
            return !node.allocated;
        }

        boolean leftFree = mergeBuddies(node.left);
        boolean rightFree = mergeBuddies(node.right);

        if (leftFree && rightFree) {
            node.left = null;
            node.right = null;
            return true;
        }

        return false;
    }

    // Get current memory usage status
    public String getMemoryStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Memory Status:\n");
        buildMemoryStatus(root, 0, status);
        return status.toString();
    }

    // Helper method to build memory status string
    private void buildMemoryStatus(Node node, int level, StringBuilder status) {
        if (node == null) return;

        String indent = "  ".repeat(level);
        status.append(indent)
              .append("Block[");
        
        if (node.allocated) {
            Integer blockId = findBlockId(node.offset, node.size);
            if (blockId != null) {
                status.append("id=").append(blockId).append(", ");
            }
        }
        
        status.append("offset=").append(node.offset)
              .append(", size=").append(node.size)
              .append(", allocated=").append(node.allocated)
              .append("]\n");

        buildMemoryStatus(node.left, level + 1, status);
        buildMemoryStatus(node.right, level + 1, status);
    }

    // Add this new method to find block ID
    private Integer findBlockId(int offset, int size) {
        for (Map.Entry<Integer, MemoryBlock> entry : allocatedBlocks.entrySet()) {
            MemoryBlock block = entry.getValue();
            if (block.getOffset() == offset && block.getSize() == size) {
                return entry.getKey();
            }
        }
        return null;
    }
} 