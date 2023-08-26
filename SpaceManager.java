import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class SpaceManager implements Serializable {
    private Allocator allocator;

    public void setAllocationMethod(Allocator allocator){
        this.allocator = allocator;
    }

    public boolean allocate(VirtualFile file){
        return allocator.allocate(file);
    }

    public void deallocate(VirtualFile file){
        if(file.isDeleted()){
            throw new RuntimeException();
        }
        allocator.deallocate(file);
    }

    public void deleteDirectory(Directory directory){
        if(directory.isDeleted()){
            throw new RuntimeException();
        }
        Queue<Directory> directories = new LinkedList<Directory>();
        directories.add(directory);
        while(!directories.isEmpty()){
            directory = directories.poll();
            directory.deleteDirectory();
            for (VirtualFile file : directory.getFiles()) {
                try{
                    deallocate(file);
                }
                catch(Exception e){

                }
            }
            for (Directory toBeDeleted : directory.getSubDirectories()) {
                directories.add(toBeDeleted);
            }
        }
    }
}
