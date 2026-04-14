package org.korastudios.amazingtitles.code.Iintegrations;

import org.korastudios.amazingtitles.code.api.AmazingTitles;

import java.io.File;

public interface Integration {

    void reload();

    default File getDataFolder() {
        return AmazingTitles.INTEGRATIONS_FOLDER;
    }

}
