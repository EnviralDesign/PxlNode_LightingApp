package frost.com.homelighting.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import frost.com.homelighting.model.Macros;

@Entity(tableName = "macro",
        indices = {@Index(value = "id")})
public class MacroEntity implements Macros{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    public MacroEntity(String name) {
        this.name = name;
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
}
