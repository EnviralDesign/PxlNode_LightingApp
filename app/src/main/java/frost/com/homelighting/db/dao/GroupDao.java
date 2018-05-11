package frost.com.homelighting.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import frost.com.homelighting.db.entity.GroupEntity;

@Dao
public interface GroupDao {
    @Query("SELECT * FROM devices_group")
    LiveData<List<GroupEntity>> loadAllGroups();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<GroupEntity> groups);

    @Query("SELECT * FROM devices_group WHERE id = :id")
    LiveData<GroupEntity> loadGroup(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertGroup(GroupEntity group);

    @Delete
    void deleteGroup(GroupEntity group);

    @Query("DELETE FROM devices_group WHERE id = :groupId")
    void deleteGroup(int groupId);
}
