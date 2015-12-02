package rda.simpleapp.model;


import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FsData implements Serializable {

    private String path;
    private String name;
    private boolean directory;
    private String node;

    public FsData() {
    }

    public FsData(Path path, String rootPath, String node) {
        this.node = node;
        this.directory = Files.isDirectory(path);
        this.path = path.toAbsolutePath().toString().replace(rootPath, "");
        this.name = path.getFileName().toString();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isDirectory() {
        return directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FsData fsData = (FsData) o;

        return path != null ? path.equals(fsData.path) : fsData.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
