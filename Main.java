import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int diskSize;
        int choice;
        String command;
        String structurePath = "DiskStructure.txt";
        Scanner scanner = new Scanner(System.in);
        File diskStructure = new File(structurePath);

        System.out.println("Choose Allocation Method:\n1- Contiguous Allocation\n2- Indexed Allocation\n3- Linked Allocation");
        choice = scanner.nextInt();
        switch (choice){
            case 1:
                FileSystem.getFileSystem().getSpaceManager().setAllocationMethod(new ContiguousAllocation());
                break;
            case 2:
                FileSystem.getFileSystem().getSpaceManager().setAllocationMethod(new IndexedAllocation());
                break;
            case 3:
                FileSystem.getFileSystem().getSpaceManager().setAllocationMethod(new LinkedAllocation());
                break;
            default:
        }

        //If disk structure doesn't exist or is empty, create it and initialize disk size
        if (diskStructure.createNewFile() || diskStructure.length() == 0) {
            System.out.println("Please insert disk size in kilobytes.");
            diskSize = scanner.nextInt();
            FileSystem.getFileSystem().setSize(diskSize);
        //If disk structure already exists, initialize file system from it
        } else {
            FileSystem.initialize(structurePath);
        }

        //After making sure our file system is correctly initialized, start taking user commands
        scanner.nextLine();
        command = scanner.nextLine();
        while(!command.equalsIgnoreCase("exit")){
            CommandsHandler.parseCommand(command);
            command = scanner.nextLine();
        }

        //Before exiting, write the disk structure onto the file
        FileOutputStream fileOut = new FileOutputStream(diskStructure);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(FileSystem.getFileSystem());
        objectOut.close();
    }
}
