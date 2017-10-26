import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;

public class DesignSqlConsumer implements Runnable {

    private final BlockingQueue<Design> designSqlQueue;

    public DesignSqlConsumer(BlockingQueue<Design> designSqlQueue) {
        this.designSqlQueue = designSqlQueue;
    }

    @Override
    public void run() {
        BufferedWriter designSqlWriter = null;
        BufferedWriter applicationSqlWriter = null;
        try {
            Path designSqlPath = Paths.get("insert-design.sql");
            Path applicationSqlPath = Paths.get("insert-application.sql");
            // 如果存在就删除
            if (Files.exists(designSqlPath)) {
                Files.delete(designSqlPath);
            }
            if (Files.exists(applicationSqlPath)) {
                Files.delete(applicationSqlPath);
            }
            // 创建file
            Files.createFile(designSqlPath);
            Files.createFile(applicationSqlPath);
            designSqlWriter = Files.newBufferedWriter(designSqlPath, StandardOpenOption.APPEND);
            applicationSqlWriter = Files.newBufferedWriter(applicationSqlPath, StandardOpenOption.APPEND);
            designSqlWriter.write("INSERT INTO design(id,owner_id, profession_id, structure_id, exhibition_type_id,open_sides,oss_key,\n" +
                    "price_lower_limit, price_upper_limit, area, status, is_visible, create_time, modified_time,primitive_name)\n" +
                    "VALUES\n");
            applicationSqlWriter.write("INSERT INTO application(design_id, applicant, status, create_time)\n" +
                    "VALUES\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (true) {
                Design design = designSqlQueue.take();
                String designValueSql = String.format("(%d,1,%d,%d,%d,%d,'%s',%d,%d,%d,1,1,%d,%d,'%s'),\n", design.getId(),
                        design.getProfessionId(), design.getStructureId(), design.getExhibitionTypeId(), design.getOpenSides(),
                        design.getOssKey(), design.getPriceLowerLimit(), design.getPriceUpperLimit(), design.getArea(),
                        System.currentTimeMillis(), System.currentTimeMillis(), design.getZipFilePrimitiveName());
                String applicationValueSql = String.format("(%d,1,2,%d),\n", design.getId(), System.currentTimeMillis());

                try {
                    designSqlWriter.write(designValueSql);
                    designSqlWriter.flush();
                    applicationSqlWriter.write(applicationValueSql);
                    applicationSqlWriter.flush();
                } catch (IOException e) {
                    System.out.println("error: exception occurs while writing sql");
                    e.printStackTrace();
                }
                System.out.println("design sql has written. design : " + design.getId()+"/"+design.getZipFilePrimitiveName());

            }
        } catch (InterruptedException e) {
            System.out.println("error: exception occurs while take from designSqlQueue");
            e.printStackTrace();
        }
    }
}
