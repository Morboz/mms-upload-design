import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;

public class SketchSqlConsumer implements Runnable {
    private final BlockingQueue<Design.Sketch> sketchSqlQueue;
    public SketchSqlConsumer(BlockingQueue<Design.Sketch> sketchSqlQueue) {
        this.sketchSqlQueue = sketchSqlQueue;
    }

    @Override
    public void run() {
        BufferedWriter sketchSqlWriter = null;
        try {
            Path sketchSqlPath = Paths.get("insert-sketch.sql");
            // 如果存在就删除
            if (Files.exists(sketchSqlPath)) {
                Files.delete(sketchSqlPath);
            }
            // 创建file
            Files.createFile(sketchSqlPath);
            sketchSqlWriter = Files.newBufferedWriter(sketchSqlPath, StandardOpenOption.APPEND);
            sketchSqlWriter.write("INSERT INTO design_sketch(design_id, oss_key, is_cover, status, create_time, primitive_name)\n" +
                    "VALUES\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (true) {
                Design.Sketch sketch = sketchSqlQueue.take();
                String sketchValueSql = String.format("(%d,'%s',%d,1,%d,'%s'),\n", sketch.getDesignId(), sketch.getOssKey(),
                        sketch.isCover()? 1 : 0, System.currentTimeMillis(),sketch.getPrimitiveName());
                try {
                    sketchSqlWriter.write(sketchValueSql);
                    sketchSqlWriter.flush();
                } catch (IOException e) {
                    System.out.println("error: exception occurs while writing sql");
                    e.printStackTrace();
                }
                System.out.println("sketch sql has written. sketch : " + sketch.getDesignId()+"/"+sketch.getPrimitiveName());
            }
        } catch (InterruptedException e) {
            System.out.println("error: exception occurs while take from designSqlQueue");
            e.printStackTrace();
        }
    }
}
