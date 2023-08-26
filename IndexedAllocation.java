public class IndexedAllocation implements Allocator{
    @Override
    public boolean allocate(VirtualFile file) {
        int blocksToAllocate = file.getSize() + 1;
        boolean[] systemBlocks = FileSystem.getFileSystem().getSystemBlocks();
        boolean allocated = true;

        //Check if there is enough space for file size + index block
        if(blocksToAllocate <= FileSystem.getFileSystem().getFreeSpace()){
            FileSystem.getFileSystem().incrementAllocatedSpace(blocksToAllocate);
            for(int i = 0; i < systemBlocks.length; i++){
                if(!systemBlocks[i]){
                    FileSystem.getFileSystem().setTrue(i);
                    file.addBlock(i);
                    blocksToAllocate--;
                    if(blocksToAllocate == 0){
                        break;
                    }
                }
            }
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
        FileSystem.getFileSystem().decrementAllocatedSpace(file.getSize() + 1);
        for (int block : file.getAllocatedBlocks()) {
            FileSystem.getFileSystem().setFalse(block);
        }
    }
}
