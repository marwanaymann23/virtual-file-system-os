import java.io.Serializable;

public interface Allocator extends Serializable {
    boolean allocate(VirtualFile file);
    void deallocate(VirtualFile file);
}
