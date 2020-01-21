package lib;

import org.powerbot.script.rt4.ClientContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public static String project = System.getProperty("user.dir");
    public static String src = project + File.separator + "src";
    public static String lib = src + File.separator + "lib";

    public static String buyerHopperAssetDir = src + File.separator + "caster" +
            File.separator + "assets";


    public static void writeTo(ClientContext ctx, String fileName, String content) throws IOException {
        FileOutputStream out = new FileOutputStream(ctx.controller.script().getStorageDirectory() + File.separator + fileName);
        out.write(content.getBytes());
        out.close();
    }
}
