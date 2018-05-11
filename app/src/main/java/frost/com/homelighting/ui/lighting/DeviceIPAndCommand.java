package frost.com.homelighting.ui.lighting;

import android.arch.persistence.room.ColumnInfo;

public class DeviceIPAndCommand {
    private String ip_address;
    private String command;

    public DeviceIPAndCommand(String ip_address, String command) {
        this.ip_address = ip_address;
        this.command = command;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
