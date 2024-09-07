package androidx.ijk.enums;

/**
 * 视频显示方式
 */
public enum Display {

    /**
     * 显示方式 - 宽度按比例显示；注意：必须设置Ratio值
     */
    RATIO_WIDTH,
    /**
     * 显示方式 - 高度按比例显示；注意：必须设置Ratio值
     */
    RATIO_HEIGHT,
    /**
     * 显示方式 - 原大小
     */
    ORIGINAL,
    /**
     * 显示方式 - 自动匹配宽高
     */
    AUTO,
    /**
     * 显示方式 - 填充宽度比例显示
     */
    MATCH_WIDTH,
    /**
     * 显示方式 - 填充高度比例显示
     */
    MATCH_HEIGHT,
    /**
     * 显示方式 - 填充宽高显示
     */
    MATCH_PARENT,

}
