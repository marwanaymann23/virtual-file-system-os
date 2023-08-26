import java.io.Serializable;
import java.util.ArrayList;

public class VirtualFile implements Serializable {
    private String filePath;
    private int size;
    private ArrayList<Integer> allocatedBlocks;
    private boolean deleted;

    public VirtualFile(int size, String filePath){
        deleted = false;
        this.size = size;
        allocatedBlocks = new ArrayList<Integer>();
        this.filePath = filePath;
    }

    public void addBlock(int block){
        allocatedBlocks.add(block);
    }

    public void deleteFile(){
        deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public int getSize(){
        return size;
    }

    public String getFilePath(){
        return filePath;
    }

    public ArrayList<Integer> getAllocatedBlocks() {
        return allocatedBlocks;
    }

}

