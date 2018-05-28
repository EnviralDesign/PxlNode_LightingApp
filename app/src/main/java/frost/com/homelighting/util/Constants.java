/*
 * Created by Ryan Aquilina on 10/18/17 5:14 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/2/17 12:01 PM
 */

package frost.com.homelighting.util;

public interface Constants {

    /* SharedPreferences Constants */
    String DEVICES_SHARED_PREFERENCES = "devices_shared_preferences";
    String PRESETS_SHARED_PREFERENCES = "presets_shared_preferences";
    String DESIGN_SHARED_PREFERENCES = "design_shared_preferences";

    /* Devices constants*/
    String ALL_DEVICES = "all_devices";
    String GROUP_OF_DEVICES_GROUPS = "group_of_devices_groups";
    String GROUP_OF_SINGLE_DEVICES = "group_of_single_devices";

    /* Presets Constants */
    String PRESET = "preset";
    String GROUP_OF_PRESETS = "group_of_presets";

    /* Macros Constants */
    String MACRO = "macro";
    String GROUP_OF_MACROS = "group_of_macros";

    /* Design Constants */
    String DESIGN_CONFIGURATION = "design_configuration";
    String DESIGN_EFFECT_PULSE = "pulse";
    String DESIGN_EFFECT_BLINK = "blink";
    String DESIGN_EFFECT_HUE = "hue";
    String DESIGN_EFFECT_HUE_TWO = "hue_two";
    String DESIGN_EFFECT_HUE_HSB = "hue_hsb";
    String DESIGN_EFFECT_HUE_HSL = "hue_hsl";
    String DESIGN_EFFECT_SPRITE = "sprite";
    String DESIGN_EFFECT_NONE = "none";
    String DESIGN_START_COLOR = "start_color";
    String DESIGN_STOP_COLOR = "stop_color";
    String DESIGN_REPETITION = "repetition";
    String DESIGN_DURATION = "duration";
    String DESIGN_CENTER_COLOR = "center_color";
    String DESIGN_CURRENT_EFFECT = "current_effect";
    String DESIGN_CURRENT_COMMAND = "current_command";
    String DESIGN_CURRENT_SPINNER_POSITION = "current_spinner_position";
    String DESIGN_SELECTED_DEVICES = "selected_devices";
    String DESIGN_START_CIRCLE_STATE = "start_circle_state";
    String DESIGN_SPRITE_SELECTION = "sprite_selection";


    /* Configuration Constants */
    String CONFIGURATION_DEVICE_NAME = "device_name";
    String CONFIGURATION_UDP_STREAMING_PORT = "udp_streaming_port";
    String CONFIGURATION_CHUNK_SIZE = "chunk_size";
    String CONFIGURATION_PIXELS_PER_STRIP = "pixels_per_strip";
    String CONFIGURATION_MA_PER_PIXEL = "ma_per_pixel";
    String CONFIGURATION_AMPS_LIMIT = "amps_limit";
    String CONFIGURATION_WARMUP_COLOR = "warmup_color";
}
