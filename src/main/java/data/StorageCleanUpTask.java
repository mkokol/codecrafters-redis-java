package data;

import java.util.TimerTask;

public class StorageCleanUpTask extends TimerTask {
    private String storageKey;

    public StorageCleanUpTask(String storageKey) {
        this.storageKey = storageKey;
    }

    @Override
    public void run() {
        Storage.delete(storageKey);
    }
}
