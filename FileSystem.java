import java.io.*;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

public class FileSystem implements Serializable {
    private SpaceManager spaceManager = new SpaceManager();
    private static FileSystem fileSystem; //singleton
    private boolean[] systemBlocks;
    private int diskSize;
    private int allocatedSpace;
    private Directory root = new Directory("root");

    private FileSystem(){

    }

    public static FileSystem getFileSystem(){
        if(fileSystem == null){
            fileSystem = new FileSystem();
        }
        return fileSystem;
    }

    public static void initialize(String path) throws IOException, ClassNotFoundException {
        File file = new File(path);
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        fileSystem = (FileSystem) objectIn.readObject();
        objectIn.close();
        fileIn.close();
    }

    public Directory getRootDirectory(){
        return root;
    }

    public SpaceManager getSpaceManager(){
        return spaceManager;
    }

    public int getFreeSpace(){
        return diskSize - allocatedSpace;
    }

    public void createFile(String path, int size){
        if(searchFile(root, path) == null || searchFile(root, path).isDeleted()){
            try{
                VirtualFile newFile = new VirtualFile(size, path);
                //If allocation succeeded add the file to parent directory
                if(spaceManager.allocate(newFile)){
                    searchDirectory(root, path.substring(0, path.lastIndexOf("/"))).addFile(newFile);
                }
            }
            catch(Exception e){
                System.out.println("Path <" + path.substring(0, path.lastIndexOf("/")) + "> doesn't exist");
            }
        }
        else{
            System.out.println("File already exists");
        }
    }

    public void createDirectory(String path){
        if(searchDirectory(root, path) == null || searchDirectory(root, path).isDeleted()){
            Directory newDirectory = new Directory(path);
            try{
                searchDirectory(root, path.substring(0, path.lastIndexOf("/"))).addSubDirectory(newDirectory);
            }
            catch(Exception e){
                System.out.println("Path <" + path.substring(0, path.lastIndexOf("/")) + "> doesn't exist");
            }
        }
        else{
            System.out.println("Folder already exists");
        }
    }

    public void displaySystemBlocks(){
        for(int i = 0; i < diskSize; i++){
            System.out.println(systemBlocks[i]);
        }
    }

    public void deleteFile(String path){
        try{
            spaceManager.deallocate(searchFile(root, path));
        }
        catch(Exception e){
            System.out.println("Path <" + path + "> doesn't exist");
        }
    }

    public void deleteFolder(String path){
        try{
            spaceManager.deleteDirectory(searchDirectory(root, path));
        }
        catch(Exception e){
            System.out.println("Path <" + path + "> doesn't exist");
        }
    }

    boolean[] getSystemBlocks(){
        return systemBlocks;
    }

    public void setTrue(int index){
        systemBlocks[index] = true;
    }

    public void setFalse(int index){
        systemBlocks[index] = false;
    }

    public void setSize(int size){
        systemBlocks = new boolean[size];
        diskSize = size;
        allocatedSpace = 0;
    }

    public int getAllocatedSpace(){
        return allocatedSpace;
    }

    public void incrementAllocatedSpace(int value){
        allocatedSpace += value;
    }

    public void decrementAllocatedSpace(int value){
        allocatedSpace -= value;
    }

    public void displayDiskStatus(){
        //Empty space
        System.out.println("Empty Space: " + (diskSize - allocatedSpace));
        //Allocated space
        System.out.println("Allocated Space: " + allocatedSpace);
        //Empty Blocks
        System.out.println("Empty Blocks:");
        for(int i = 0; i < diskSize; i++){
            if(!systemBlocks[i]){
                System.out.print(i + "  ");
            }
        }
        System.out.println();
        //Allocated Blocks
        System.out.println("Allocated Blocks:");
        for(int i = 0; i < diskSize; i++){
            if(systemBlocks[i]){
                System.out.print(i + "  ");
            }
        }
        System.out.println();
    }

    public Directory searchDirectory(Directory currentDirectory, String path){
        Queue<Directory> directories = new LinkedList<Directory>();
        directories.add(currentDirectory);
        while(!directories.isEmpty()){
            currentDirectory = directories.poll();
            if(currentDirectory.getDirectoryPath().equals(path)){
                return currentDirectory;
            }
            for (Directory directory : currentDirectory.getSubDirectories()){
                directories.add(directory);
            }
        }
        return null;
    }

    public VirtualFile searchFile(Directory currentDirectory, String path){
        Queue<Directory> directories = new LinkedList<Directory>();
        directories.add(currentDirectory);
        while(!directories.isEmpty()){
            currentDirectory = directories.poll();
            for (VirtualFile file : currentDirectory.getFiles()){
                if(file.getFilePath().equals(path)){
                    return file;
                }
            }
            for (Directory directory : currentDirectory.getSubDirectories()){
                directories.add(directory);
            }
        }
        return null;
    }
}
