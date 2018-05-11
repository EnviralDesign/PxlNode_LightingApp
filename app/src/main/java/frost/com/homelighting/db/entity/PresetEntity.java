package frost.com.homelighting.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import frost.com.homelighting.model.Presets;

@Entity(tableName = "preset",
        indices = {@Index(value = "id")})
public class PresetEntity implements Presets{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String command;

    public PresetEntity(String name, String command) {
        this.name = name;
        this.command = command;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
