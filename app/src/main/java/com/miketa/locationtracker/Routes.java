package com.miketa.locationtracker;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Routes {


    /*
    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }*/

    /**
     * A dummy item representing a piece of content.
     */

    public final String wholeJson;
    public final String id;
    public final String content;

    public Routes(String id, String content, String wholeJson) {
        this.id = id;
        this.content = content;
        this.wholeJson = wholeJson;
    }

    @Override
    public String toString() {
            return content;
        }

}
