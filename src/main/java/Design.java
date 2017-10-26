import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Design {
    public Design() {
    }
    public Design(Path designDir) {
//        this.zipFileIn = Files.newInputStream()
        List<Path> zipFile = null;
        try {
            zipFile = Files.list(designDir)
                    .filter(filePath -> !Files.isDirectory(filePath))
                    .filter(filePath -> filePath.getFileName().toString().endsWith(".zip") || filePath.getFileName().toString().endsWith(".rar"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception occurs when create Design");
        }
        if (zipFile.size() != 1) {
            System.out.println("error: more than one zip files found in the directory: " + designDir.getFileName());
        }
        // zip file
        this.zipFilePrimitiveName = zipFile.get(0).getFileName().toString();
        try {
//            this.zipFileIn = Files.newInputStream(zipFile.get(0));
            this.zipFile = new File(zipFile.get(0).toString());
            this.zipFileSize = Files.size(zipFile.get(0));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception occurs when get zip file input stream");
        }
        // oss key
        this.ossKey = designDir.getFileName().toString() + "/zip-file";

        // set data
        String[] splitPath = designDir.getFileName().toString().split("-");
        this.id = Integer.parseInt(splitPath[0]) + 10000;
        this.priceLowerLimit = PriceRangeEnum.getPriceLowerLimitBySymbol(splitPath[1]);
        this.priceUpperLimit = PriceRangeEnum.getPriceUpperLimitBySymbol(splitPath[1]);
        this.area = Integer.parseInt(splitPath[2]);
        this.exhibitionTypeId = Byte.parseByte(splitPath[3].substring(1));
        this.structureId = Structure.getStructureIdBySymbol(splitPath[4]);
        this.openSides = Byte.parseByte(splitPath[5].substring(1));
        this.professionId = Profession.getProfessionIdBySymbol(splitPath[6]);
        // set sketch
        List<Path> sketchPaths = null;
        try {
            sketchPaths = Files.list(designDir)
                    .filter(filePath -> !Files.isDirectory(filePath))
                    .filter(filePath -> filePath.getFileName().toString().endsWith(".jpg"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception occurs when trying to get sketches' path");
        }
        this.sketches = getSketchListFromPaths(sketchPaths, designDir, id);

    }

    private List<Sketch> getSketchListFromPaths(List<Path> sketchPaths, Path designDir, int designId) {
        List<Sketch> result = new ArrayList<>();
        sketchPaths.forEach(path -> result.add(new Sketch(path, designDir, designId)));
        return result;
    }

    private int id;
    private InputStream zipFileIn;
    private File zipFile;

    private String zipFilePrimitiveName;
    private int priceLowerLimit;
    private int priceUpperLimit;
    private int area;
    private byte exhibitionTypeId;
    private byte structureId;
    private byte openSides;
    private byte professionId;

    private String ossKey;
    private long zipFileSize;

    private List<Sketch> sketches;

    public File getZipFile() {
        return zipFile;
    }

    public long getZipFileSize() {
        return zipFileSize;
    }

    public int getId() {
        return id;
    }

    public int getArea() {
        return area;
    }


    public String getOssKey() {
        return ossKey;
    }


    public List<Sketch> getSketches() {
        return sketches;
    }

    public InputStream getZipFileIn() {
        return zipFileIn;
    }

    public String getZipFilePrimitiveName() {
        return zipFilePrimitiveName;
    }

    public int getPriceLowerLimit() {
        return priceLowerLimit;
    }

    public int getPriceUpperLimit() {
        return priceUpperLimit;
    }


    public byte getExhibitionTypeId() {
        return exhibitionTypeId;
    }


    public byte getStructureId() {
        return structureId;
    }

    public byte getOpenSides() {
        return openSides;
    }

    public byte getProfessionId() {
        return professionId;
    }

    static class Sketch{
        public Sketch() {
        }
        Sketch(Path path, Path designDir, int designId) {
            this.designId = designId;
            if (Files.isDirectory(path))
                throw new RuntimeException("sketch path is a directory");
            this.primitiveName = path.getFileName().toString();
            try {
//                this.sketchFileIn = Files.newInputStream(path);
                this.sketchFile = new File(path.toString());
                this.fileSize = Files.size(path);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Exception occurs when create Sketch");
            }
            if (primitiveName.startsWith("04")){
                this.cover = true;
            }
            this.ossKey = designDir.getFileName().toString() + "/" + "sketch" + primitiveName.substring(0, 2);
        }
        private String primitiveName;
        private InputStream sketchFileIn;
        private File sketchFile;
        private boolean cover;
        private String ossKey;
        private int designId;

        private long fileSize;

        public File getSketchFile() {
            return sketchFile;
        }

        public long getFileSize() {
            return fileSize;
        }

        public int getDesignId() {
            return designId;
        }

        public String getOssKey() {
            return ossKey;
        }

        public String getPrimitiveName() {
            return primitiveName;
        }

        public void setPrimitiveName(String primitiveName) {
            this.primitiveName = primitiveName;
        }

        public InputStream getSketchFileIn() {
            return sketchFileIn;
        }

        public void setSketchFileIn(InputStream sketchFileIn) {
            this.sketchFileIn = sketchFileIn;
        }

        public boolean isCover() {
            return cover;
        }

        public void setCover(boolean cover) {
            this.cover = cover;
        }
    }
}
