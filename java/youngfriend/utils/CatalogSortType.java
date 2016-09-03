package youngfriend.utils;

/**
 * Created by xiong on 2/19/16.
 */
public enum CatalogSortType {
    LEAF {
        @Override
        String getValue() {
            return "0";
        }
    }, PRE_SIBLING {
        @Override
        String getValue() {
            return "1";
        }
    }, NEXT_SIBLING {
        @Override
        String getValue() {
            return "2";
        }
    };

    abstract String getValue();
}
