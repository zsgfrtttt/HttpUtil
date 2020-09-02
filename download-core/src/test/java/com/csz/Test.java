package com.csz;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Test {
    public static void main(String[] args){
        Schema schema = new Schema(1,"com.csz.okhttp");
        Entity downloadEntity = schema.addEntity("DownloadEntity");
        downloadEntity.addLongProperty("start_position");
        downloadEntity.addLongProperty("progress_position");
        downloadEntity.addLongProperty("end_position");
        downloadEntity.addStringProperty("download_url");
        downloadEntity.addIntProperty("thread_id");
        downloadEntity.addIdProperty().autoincrement();

        try {
            new DaoGenerator().generateAll(schema,"Okhttp/src/gen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
