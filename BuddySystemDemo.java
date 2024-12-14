import java.util.Scanner;

public class BuddySystemDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Welcome to Buddy System Memory Allocator");
        System.out.print("Enter total memory size (must be a power of 2): ");
        int totalSize = scanner.nextInt();

        try {
            BuddySystem buddySystem = new BuddySystem(totalSize);
            
            while (true) {
                System.out.println("\nBuddy System Memory Allocator");
                System.out.println("1. Allocate memory");
                System.out.println("2. Free memory");
                System.out.println("3. Show memory status");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Enter size to allocate: ");
                        int size = scanner.nextInt();
                        BuddySystem.MemoryBlock block = buddySystem.allocate(size);
                        if (block != null) {
                            System.out.println("Allocated block ID: " + block.getId() +
                                            " (size: " + block.getSize() +
                                            ", offset: " + block.getOffset() + ")");
                        } else {
                            System.out.println("Failed to allocate memory");
                        }
                        break;

                    case 2:
                        System.out.print("Enter block ID to free: ");
                        int blockId = scanner.nextInt();
                        if (buddySystem.free(blockId)) {
                            System.out.println("Successfully freed block " + blockId);
                        } else {
                            System.out.println("Failed to free block " + blockId);
                        }
                        break;

                    case 3:
                        System.out.println(buddySystem.getMemoryStatus());
                        break;

                    case 4:
                        System.out.println("Exiting...");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
} 