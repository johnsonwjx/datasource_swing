package youngfriend.utils;

/**
 * Created by xiong on 2/23/16.
 */
public enum ModuleType {
    COMMON {
        @Override
        public String getValue() {
            return "0";
        }

        @Override
        public String getServiceType() {
            return "common";
        }

        @Override
        public String getName() {
            return "通用数据源";
        }
    }, SERVICE {
        @Override
        public String getValue() {
            return "1";
        }

        @Override
        public String getServiceType() {
            return "dataSource";
        }

        @Override
        public String getName() {
            return "专用服务数据源";

        }
    }, BUTTON {
        @Override
        public String getValue() {
            return "2";
        }

        @Override
        public String getServiceType() {
            return "buttonEvent";
        }

        @Override
        public String getName() {
            return "专用业务组件";
        }
    }, COMMON_UPDATE {
        @Override
        public String getValue() {
            return "3";
        }

        @Override
        public String getServiceType() {
            return "commonUpdate";
        }

        @Override
        public String getName() {
            return "通用更新组件";
        }
    };

    public abstract String getValue();

    public abstract String getServiceType();

    public abstract String getName();
}
