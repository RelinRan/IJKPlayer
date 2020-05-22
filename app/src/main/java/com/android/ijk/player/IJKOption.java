package com.android.ijk.player;

/**
 * Author: Relin
 * Describe:ijk参数
 * Date:2020/5/12 11:05
 */
public class IJKOption<T> {

    /**
     * 分类
     */
    private int category;
    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private T value;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
