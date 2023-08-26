import java.util.ArrayList;

public class LinkedAllocation implements Allocator{
    @Override
    public boolean allocate(VirtualFile file) {
        int blocksToAllocate = file.getSize();
        boolean[] systemBlocks = FileSystem.getFileSystem().getSystemBlocks();
        boolean allocated = true;
        int start , end = 0;
        if(blocksToAllocate <= FileSystem.getFileSystem().getFreeSpace()){
            FileSystem.getFileSystem().incrementAllocatedSpace(blocksToAllocate);
            ArrayList<Integer> tempBlocks = new ArrayList<Integer>();
            for(int i = 0; i < systemBlocks.length; i++){
                if(!systemBlocks[i]){
                    FileSystem.getFileSystem().setTrue(i);
                    tempBlocks.add(i);
                    blocksToAllocate--;
                    if(blocksToAllocate == 0){
                        end = i;
                        break;
                    }
                }
            }
            start = tempBlocks.get(0);
            tempBlocks.remove(0);
            file.addBlock(start);
            file.addBlock(end);
            file.addBlock(start);
            for (int index : tempBlocks){
                file.addBlock(index);
                file.addBlock(index);
            }
            file.addBlock(-1);
        }
        else{
            System.out.println("Not enough space");
            allocated = false;
        }
        return allocated;
    }

    @Override
    public void deallocate(VirtualFile file) {
        file.deleteFile();
        FileSystem.getFileSystem().decrementAllocatedSpace(file.getSize());
        file.getAllocatedBlocks().remove(file.getAllocatedBlocks().size() - 1);
        for (int block : file.getAllocatedBlocks()) {
            FileSystem.getFileSystem().setFalse(block);
        }
    }
}
