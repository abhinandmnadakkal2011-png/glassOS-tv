package com.gathin.tvhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Choices made on the TV in LauncherSettingsActivity: app order, hidden apps, dock and clock sizes.
 * They sit on top of config.xml / a theme; every write bumps a revision so Home knows to rebuild.
 */
final class LauncherPrefs {

    static final int DEF_ICON_DP = 66, DEF_GAP_DP = 18, DEF_VISIBLE = 8, DEF_BOTTOM_DP = 18, DEF_CLOCK_SP = 88,
            DEF_DATE_SP = 19, DEF_CLOCK_TOP_DP = 56, DEF_CLOCK_WEIGHT = 400,
            DEF_GLASS_EDGE = 3, DEF_GLASS_BRIGHT = 100, DEF_SHADE = 70, DEF_SHADE_HEIGHT = 260,
            DEF_CLOCK_SHADOW = 50, DEF_WALL_DIM = 15, DEF_ICON_THEME = 0, DEF_ICON_TINT = 0;

    private static final String FILE = "launcher_settings";
    private static final String ORDER = "order", HIDDEN = "hidden";
    static final String REV = "rev";
    static final String ICON = "icon_dp", GAP = "gap_dp", VISIBLE = "visible", BOTTOM = "bottom_dp",
            CLOCK = "clock_sp", SHOW_CLOCK = "show_clock", SHOW_DATE = "show_date", DATE = "date_sp",
            CLOCK_TOP = "clock_top_dp", CLOCK_WEIGHT = "clock_weight", ICON_GLASS = "icon_glass",
            GLASS_EDGE = "glass_edge", GLASS_BRIGHT = "glass_bright", SHADE = "bottom_shade",
            SHADE_HEIGHT = "bottom_shade_dp", CLOCK_SHADOW = "clock_shadow", WALLPAPER = "wallpaper",
            WALL_DIM = "wallpaper_dim", GLOW = "app_glow", WALL_FILE = "wallpaper_file",
            ICON_THEME = "icon_theme", ICON_TINT = "icon_tint";

    /** Keys Home applies in place with a crossfade instead of rebuilding itself. */
    static final java.util.Set<String> WALLPAPER_KEYS = new java.util.HashSet<>(
            java.util.Arrays.asList(WALLPAPER, WALL_FILE, WALL_DIM));

    private final SharedPreferences sp;

    private LauncherPrefs(Context ctx) {
        sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    /** The underlying store, for Home to listen to while the settings panel is open over it. */
    static SharedPreferences store(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    static LauncherPrefs get(Context ctx) {
        return new LauncherPrefs(ctx.getApplicationContext());
    }

    int rev() {
        return sp.getInt(REV, 0);
    }

    int iconDp() {
        return sp.getInt(ICON, DEF_ICON_DP);
    }

    int gapDp() {
        return sp.getInt(GAP, DEF_GAP_DP);
    }

    int visible() {
        return sp.getInt(VISIBLE, DEF_VISIBLE);
    }

    int bottomDp() {
        return sp.getInt(BOTTOM, DEF_BOTTOM_DP);
    }

    int clockSp() {
        return sp.getInt(CLOCK, DEF_CLOCK_SP);
    }

    /** Outfit weight for the clock digits, 100 (hairline) to 900 (black). */
    int clockWeight() {
        return sp.getInt(CLOCK_WEIGHT, DEF_CLOCK_WEIGHT);
    }

    int dateSp() {
        return sp.getInt(DATE, DEF_DATE_SP);
    }

    /** Distance from the top of the screen to the top of the date/clock block. */
    int clockTopDp() {
        return sp.getInt(CLOCK_TOP, DEF_CLOCK_TOP_DP);
    }

    boolean showClock() {
        return sp.getBoolean(SHOW_CLOCK, true);
    }

    boolean showDate() {
        return sp.getBoolean(SHOW_DATE, true);
    }

    int getInt(String key, int def) {
        return sp.getInt(key, def);
    }

    boolean getBool(String key) {
        return sp.getBoolean(key, true);
    }

    /** Packages in the order the owner arranged them; apps not listed follow in the default order. */
    List<String> order() {
        String s = sp.getString(ORDER, "");
        return s.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(s.split(",")));
    }

    Set<String> hidden() {
        return new HashSet<>(sp.getStringSet(HIDDEN, new HashSet<>()));
    }

    String getString(String key) {
        return sp.getString(key, "");
    }

    /** A wallpaper choice: a built-in/none/theme code, or Wallpapers.FILE with the picture's file name. */
    void putWallpaper(int choice, String file) {
        sp.edit().putInt(WALLPAPER, choice).putString(WALL_FILE, file == null ? "" : file)
                .putInt(REV, rev() + 1).apply();
    }

    void putInt(String key, int value) {
        sp.edit().putInt(key, value).putInt(REV, rev() + 1).apply();
    }

    void putBool(String key, boolean value) {
        sp.edit().putBoolean(key, value).putInt(REV, rev() + 1).apply();
    }

    void putOrder(List<String> pkgs) {
        sp.edit().putString(ORDER, TextUtils.join(",", pkgs)).putInt(REV, rev() + 1).apply();
    }

    void putHidden(Set<String> pkgs) {
        sp.edit().putStringSet(HIDDEN, new HashSet<>(pkgs)).putInt(REV, rev() + 1).apply();
    }

    void reset() {
        int next = rev() + 1;
        sp.edit().clear().putInt(REV, next).apply();
    }

    /**
     * The dock's order: the owner's arrangement first, then config dock_apps, then alphabetical.
     * Used by Home and by the settings list so both show the same sequence.
     */
    static int compare(String pkgA, String labelA, String pkgB, String labelB,
                       List<String> order, List<String> favourites) {
        int oa = order.indexOf(pkgA), ob = order.indexOf(pkgB);
        if (oa >= 0 || ob >= 0) {
            if (oa < 0) return 1;
            if (ob < 0) return -1;
            return Integer.compare(oa, ob);
        }
        int fa = favourites.indexOf(pkgA), fb = favourites.indexOf(pkgB);
        if (fa >= 0 || fb >= 0) {
            if (fa < 0) return 1;
            if (fb < 0) return -1;
            return Integer.compare(fa, fb);
        }
        return labelA.compareToIgnoreCase(labelB);
    }
}
