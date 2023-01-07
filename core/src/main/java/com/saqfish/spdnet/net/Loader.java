package com.saqfish.spdnet.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.saqfish.spdnet.Assets;
import com.saqfish.spdnet.ShatteredPixelDungeon;
import com.saqfish.spdnet.net.windows.WndDownloadStatus;
import com.saqfish.spdnet.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class Loader {
    private String root;
    //TODO: load asset root from server
    private static String defaultRoot = "https://saqfish.com/assets/";

    private List<String> assets;

    private Window confirmWindow;
    private WndDownloadStatus statusWindow;

    private int count;
    private int failures;
    private int successes;

    Loader(String root){
        this.root = root;
        this.assets = new ArrayList<String>();
    }

    Loader(){
        this(defaultRoot);
    }

    public void download(String from, String to){
        Game.runOnRenderThread(()->
                Pixmap.downloadFromUrl(from, new Pixmap.DownloadPixmapResponseListener() {
                    @Override
                    public void downloadComplete(Pixmap pixmap) {
                        FileHandle file = Gdx.files.external(FileUtils.getDefaultPath() + to);
                        PixmapIO.writePNG(file, pixmap);
                        statusWindow.addFile(from, true, ++successes);
                        statusWindow.complete((successes+failures) == count);
                        DeviceCompat.log("Assets <-", from);
                        DeviceCompat.log("Assets ->", to);
                    }

                    @Override
                    public void downloadFailed(Throwable t) {
                        statusWindow.addFile(from, false, ++failures);
                        statusWindow.complete((successes+failures) == count);
                    }

                })
        );
    }

    public void addToAssets(List<String> list, Class c){
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            try {
                list.add( (String) field.get(c));
                count++;
            } catch (IllegalAccessException ignored) { }
        }
    }

    public void downloadAllAssets(){
        addToAssets(assets, Assets.Environment.class);
        addToAssets(assets, Assets.Sprites.class);
        addToAssets(assets, Assets.Interfaces.class);

        Game.runOnRenderThread(() -> {
            for (String file : assets) {
                if(statusWindow == null) {
                    statusWindow = new WndDownloadStatus();
                    ShatteredPixelDungeon.scene().add(statusWindow);
                }
                download(root+file, file);
            }
        });
    }

    public void clear(){
        assets.clear();
    }
}
