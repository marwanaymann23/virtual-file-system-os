public class ContiguousAllocation implements Allocator{
    @Override
    public boolean allocate(VirtualFile file) {
        boolean[] systemBlocks = FileSystem.getFileSystem().getSystemBlocks();
        int index = -1;
        int freeBlocks = 0;
        int tempSize = Integer.MAX_VALUE;
        boolean allocated = true;

        //Get the starting index of the best fit in memory
        for (int i = 0; i < systemBlocks.length; i++){
            freeBlocks = 0;
            //If current block is full continue
            if(systemBlocks[i]){
               continue;
            }
            else{
                //If current block is empty, loop te get its size
                int j;
                for(j = i; j < systemBlocks.length; j++){
                    if(systemBlocks[j]){
                        //i = j - 1;
                        break;
                    }
                    freeBlocks++;
                }
                //If size is sufficient for the file and is smaller than tempSize, update tempSize and index
                if(freeBlocks >= file.getSize() && freeBlocks < tempSize){
                    index = i;
                    tempSize = freeBlocks;
                }
                i = j - 1;
            }
        }

        //After determining the start index, start allocating blocks
        if(index == -1){
            System.out.println("Not enough space");
            allocated = false;
        }
        else{
            FileSystem.getFileSystem().incrementAllocatedSpace(file.getSize());
            for(int i = index; i < file.getSize() + index; i++){
                FileSystem.getFileSystem().setTrue(i);
            }
            file.addBlock(index);
            file.addBlock(file.getSize());
        }
        return allocated;
    }

    @Override
    public void deallocate(VirtualFile file) {
        int start = file.getAllocatedBlocks().get(0);
        int size = file.getAllocatedBlocks().get(1);
        file.deleteFile();
        FileSystem.getFileSystem().decrementAllocatedSpace(file.getSize());
        for (int i = start; i < start + size; i++) {
            FileSystem.getFileSystem().setFalse(i);
        }
    }
}
